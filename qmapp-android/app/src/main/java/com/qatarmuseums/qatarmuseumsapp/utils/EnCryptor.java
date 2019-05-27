package com.qatarmuseums.qatarmuseumsapp.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import timber.log.Timber;

import static com.qatarmuseums.qatarmuseumsapp.Config.ANDROID_KEY_STORE;
import static com.qatarmuseums.qatarmuseumsapp.Config.ANDROID_KEY_STORE_BELOW_M;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_ENCRYPTION_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_KEY_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.QM_ALIAS_VECTOR_SUFFIX;
import static com.qatarmuseums.qatarmuseumsapp.Config.TRANSFORMATION;

public class EnCryptor {

    private byte[] encryption;
    private byte[] initVector;
    private Cipher cipher;
    private KeyPair keyPair;
    private Key publicKey;
    private Key privateKey;
    private String base64Encrypted;
    private SharedPreferences sharedPreferences;


    public EnCryptor() {
    }

    @SuppressLint("InlinedApi")
    public byte[] encryptText(final String alias, final String textToEncrypt, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));
                initVector = cipher.getIV();
                encryption = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

                String base64InitVector = Base64.encodeToString(initVector, Base64.NO_WRAP);
                sharedPreferences.edit().putString(alias + QM_ALIAS_VECTOR_SUFFIX, base64InitVector).apply();

            } else {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE_BELOW_M);
                keyPairGenerator.initialize(1024);
                keyPair = keyPairGenerator.generateKeyPair();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
                cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                ProfileDetails profileDetails = new ProfileDetails();
                encryption = cipher.doFinal(textToEncrypt.getBytes());

                String base64PublicKey = Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP);
                sharedPreferences.edit().putString(alias + QM_ALIAS_KEY_SUFFIX, base64PublicKey).apply();
            }
            base64Encrypted = Base64.encodeToString(encryption, Base64.NO_WRAP);
            sharedPreferences.edit().putString(alias + QM_ALIAS_ENCRYPTION_SUFFIX, base64Encrypted).apply();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException e) {
            Timber.e("onClick() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return encryption;
    }

    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator;
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());
        return keyGenerator.generateKey();
    }

}
