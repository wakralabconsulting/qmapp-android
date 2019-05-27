package com.qatarmuseums.qatarmuseumsapp.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import timber.log.Timber;

import static com.qatarmuseums.qatarmuseumsapp.Config.ANDROID_KEY_STORE;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_ENCRYPTION_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_KEY_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_VECTOR_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.TRANSFORMATION;

public class DeCryptor {

    private KeyStore keyStore;
    private SharedPreferences qmPreferences;
    private byte[] encryptionIv;
    private byte[] encryptedData;
    private String base64Encryption;

    public DeCryptor() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    @SuppressLint("InlinedApi")
    public String decryptData(final String alias, Context context) {
        Cipher cipher = null;
        String decryptedData = null;
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        base64Encryption = qmPreferences.getString(alias + QM_ALIAS_ENCRYPTION_SUFFIX, null);
        if (base64Encryption != null) {
            encryptedData = Base64.decode(base64Encryption, Base64.NO_WRAP);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String base64InitVector = qmPreferences.getString(alias + QM_ALIAS_VECTOR_SUFFIX, null);
                    if (TextUtils.isEmpty(base64InitVector) || TextUtils.isEmpty(base64Encryption))
                        throw new NullPointerException("Not found initialization vector or encryption data for specified alias");

                    encryptionIv = Base64.decode(base64InitVector, Base64.NO_WRAP);

                    cipher = Cipher.getInstance(TRANSFORMATION);
                    final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
                    cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);
                    decryptedData = new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
                } else {
                    String base64PublicKey = qmPreferences.getString(alias + QM_ALIAS_KEY_SUFFIX, null);
                    byte[] keyBytes = Base64.decode(base64PublicKey, Base64.NO_WRAP);

                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

                    try {
                        KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
                        PublicKey key;
                        key = keyFactory.generatePublic(keySpec);

                        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
                        cipher.init(Cipher.DECRYPT_MODE, key);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    decryptedData = new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
                }
            } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                    KeyStoreException | NoSuchPaddingException | InvalidKeyException e) {
                Timber.e("decryptData() called with: " + e.getMessage(), e);
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            return decryptedData;
        } else
            return null;
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}
