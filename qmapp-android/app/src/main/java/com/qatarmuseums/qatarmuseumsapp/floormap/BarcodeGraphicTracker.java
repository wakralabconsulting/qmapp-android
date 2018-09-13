package com.qatarmuseums.qatarmuseumsapp.floormap;

import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.qatarmuseums.qatarmuseumsapp.floormap.camera.GraphicOverlay;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
class BarcodeGraphicTracker extends Tracker<Barcode> {
    private static final String TAG = "BarcodeGraphicTracker";
    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;

    public static final String SIP_USER_NAME = "sipUserName";
    public static final String SIP_PASSWORD = "sipPassword";
    public static final String COMPANY_NAME = "realm";

    public final static int LOGIN_DATA_MESSAGE = 1;

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic) {
        mOverlay = overlay;
        mGraphic = graphic;
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Barcode item) {
        mGraphic.setId(id);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        HashMap<String, String> loginDict = new HashMap<String, String>();

        String secret = "3@!AK/C4kj.BRUd4";
        String cipherText = item.rawValue;

        String decryptedText = "";
        try {
            decryptedText = cipherText;
            System.out.println("cypher "+ decryptedText );
            Log.d(TAG, decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] parts = decryptedText.split(",");
        if (parts.length == 3) {
            for (String part : parts) {
                String[] dataBlock = part.split(":");
                if (dataBlock.length == 2)
                    switch (dataBlock[0]) {
                        case SIP_USER_NAME:
                            loginDict.put(SIP_USER_NAME, dataBlock[1]);
                            break;
                        case SIP_PASSWORD:
                            loginDict.put(SIP_PASSWORD, dataBlock[1]);
                            break;
                        case COMPANY_NAME:
                            loginDict.put(COMPANY_NAME, dataBlock[1]);
                            break;
                    }
            }
        }
        if (loginDict.size() == 3) {
            mOverlay.add(mGraphic);
            mGraphic.updateItem(item);
            if (BarCodeCaptureActivity.handler_ != null) {
                Message msg = Message.obtain(BarCodeCaptureActivity.handler_, LOGIN_DATA_MESSAGE, loginDict);
                msg.sendToTarget();
            }
        }
    }

    /**
     * Generates a key and an initialization vector (IV) with the given salt and password.
     * <p>
     * This method is equivalent to OpenSSL's EVP_BytesToKey function
     * (see https://github.com/openssl/openssl/blob/master/crypto/evp/evp_key.c).
     * By default, OpenSSL uses a single iteration, MD5 as the algorithm and UTF-8 encoded password data.
     * </p>
     *
     * @param keyLength  the length of the generated key (in bytes)
     * @param ivLength   the length of the generated IV (in bytes)
     * @param iterations the number of digestion rounds
     * @param salt       the salt data (8 bytes of data or <code>null</code>)
     * @param password   the password data (optional)
     * @param md         the message digest algorithm to use
     * @return an two-element array with the generated key and IV
     */
    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte) 0);
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }

    public interface BarcodeGraphicTrackerListener {
        void onScanned(Barcode barcode);

        void onScannedMultiple(List<Barcode> barcodes);

        void onBitmapScanned(SparseArray<Barcode> sparseArray);

        void onScanError(String errorMessage);
    }
}
