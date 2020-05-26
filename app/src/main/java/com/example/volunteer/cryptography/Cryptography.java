package com.example.volunteer.cryptography;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    public static String decrypt(String[] secret) throws Exception {
        byte[] iv = "itisaninitvector".getBytes("UTF-8");
        byte[] key = "thisstringisdope".getBytes("UTF-8");

        byte[] secret_in_bytes = new byte[secret.length];
        for (int i = 0; i < secret_in_bytes.length; i++) {
            secret_in_bytes[i] = (byte) Integer.parseInt(secret[i], 16);
        }
        SecretKey originalKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, originalKey, new IvParameterSpec(iv));
        byte[] decryptedMessage = cipher.doFinal(secret_in_bytes);
        return new String(decryptedMessage);
    }


    public static void storeEncryptedSecret(Context context, String decryptedSecret) throws Exception {

        // Set up the Cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey());

        // Store the IV
        SharedPreferences sharedPreferences = context.getSharedPreferences("privateData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("iv", Base64.encodeToString(cipher.getIV(), Base64.DEFAULT));

        // Store the secret encrypted
        editor.putString("encrypted_secret", Base64.encodeToString(cipher.doFinal(decryptedSecret.getBytes("UTF-8")), Base64.DEFAULT));
        editor.apply();
    }

    private static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder("secret_keys", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        return keyGenerator.generateKey();
    }

    public static String getHashed(String unhashedString) throws Exception {
        int iterations = 10000;
        char chars[] = unhashedString.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return Base64.encodeToString(skf.generateSecret(spec).getEncoded(), Base64.DEFAULT);

    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private static byte[] getSalt() {
        return Base64.decode("NZsP6NnmfBuYeJrrAKNuVQ==", Base64.DEFAULT);
    }

    public static String getDecryptedSecret(Context context) throws Exception {
        SecretKey secretKey = getSecretKey();
        SharedPreferences sharedPreferences = context.getSharedPreferences("privateData", Context.MODE_PRIVATE);
        byte[] iv = Base64.decode(sharedPreferences.getString("iv", null), Base64.DEFAULT);
        byte[] encrypted_secret = Base64.decode(sharedPreferences.getString("encrypted_secret", null), Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

        return new String(cipher.doFinal(encrypted_secret), "UTF-8");
    }

    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("secret_keys", null);

        return secretKeyEntry.getSecretKey();
    }

}
