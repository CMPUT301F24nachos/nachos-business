package com.example.nachosbusiness;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QRUtil {

    private static final String TAG = "QRUtil";

    /**
     * QR utility is used to generate, display and hash QR codes
     */
    public QRUtil() {
    }

    /**
     * Generate a bitmap version of the event QR code
     * @param eventID String of the event ID for the QR code to link to
     * @return bitmap value of QR code
     */
    public Bitmap generateQRCode(String eventID) {
        String deepLinkUrl = "nachos-business://event/" + eventID;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Bitmap bitmap = null;

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(deepLinkUrl, BarcodeFormat.QR_CODE, 512, 512);
            bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);

            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            Log.e(TAG, e.toString());
        }
        return bitmap;
    }

    /**
     * Display the generated QR code in an Imageview object.
     * @param qrBitmap Bitmap value of QR code
     * @param imageView ImageView object to display the QR code to
     */
    public void display(Bitmap qrBitmap, ImageView imageView){
        imageView.setImageBitmap(qrBitmap);
    }

    /**
     * Return a hash of the QR code data
     * @param eventId EventID that will be linked to in the hash value
     * @return hashed value using SHA-256 encryption
     */
    public String hashQRCodeData(String eventId) {
        String deepLinkUrl = "nachos-business://event/" + eventId;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(deepLinkUrl.getBytes());

            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
