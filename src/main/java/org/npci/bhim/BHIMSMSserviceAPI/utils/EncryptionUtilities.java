package org.npci.bhim.BHIMSMSserviceAPI.utils;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public  class EncryptionUtilities {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final SecureRandom secureRandom = new SecureRandom();



    // --------- Key Generation & Loading ----------
    public static String generateBase64Key128() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey key = kg.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey secretKeyFromBase64(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 16) throw new IllegalArgumentException("Key must be 128-bit (16 bytes)");
        return new SecretKeySpec(keyBytes, "AES");
    }

    // --------- Core Encrypt / Decrypt ----------
    public static String encryptToBase64(SecretKey key, byte[] plainBytes, @Nullable byte[] aad) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        if (aad != null) cipher.updateAAD(aad);

        byte[] cipherBytes = cipher.doFinal(plainBytes);

        byte[] out = new byte[iv.length + cipherBytes.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cipherBytes, 0, out, iv.length, cipherBytes.length);

        return Base64.getEncoder().encodeToString(out);
    }

    public static byte[] decryptFromBase64(SecretKey key, String base64IvCiphertext,@Nullable byte[] aad) throws Exception {
        byte[] ivCipher = Base64.getDecoder().decode(base64IvCiphertext);
        if (ivCipher.length < IV_LENGTH_BYTES) throw new IllegalArgumentException("Invalid input");

        byte[] iv = new byte[IV_LENGTH_BYTES];
        System.arraycopy(ivCipher, 0, iv, 0, IV_LENGTH_BYTES);
        byte[] cipherBytes = new byte[ivCipher.length - IV_LENGTH_BYTES];
        System.arraycopy(ivCipher, IV_LENGTH_BYTES, cipherBytes, 0, cipherBytes.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        if (aad != null) cipher.updateAAD(aad);

        return cipher.doFinal(cipherBytes);
    }
    // ---------------------- Field Helpers ----------

    // --------- Field Helpers with optional AAD ----------

    public static String encryptField(SecretKey key, String plainText) throws Exception {
        return encryptField(key, plainText, null); // default without AAD
    }

    public static String encryptField(SecretKey key, String plainText, String aad) throws Exception {
        if (plainText == null) return null;
        byte[] aadBytes = (aad != null) ? aad.getBytes(StandardCharsets.UTF_8) : null;
        return encryptToBase64(key, plainText.getBytes(StandardCharsets.UTF_8), aadBytes);
    }

    public static String decryptField(SecretKey key, String encryptedText) throws Exception {
        return decryptField(key, encryptedText, null); // default without AAD
    }

    public static String decryptField(SecretKey key, String encryptedText, String aad) throws Exception {
        if (encryptedText == null) return null;
        byte[] aadBytes = (aad != null) ? aad.getBytes(StandardCharsets.UTF_8) : null;
        byte[] decrypted = decryptFromBase64(key, encryptedText, aadBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }


    // --------- Optional: pack/unpack with keyId ----------
    public static String packWithKeyId(String keyId, String base64IvCipher) {
        return keyId + ":" + base64IvCipher;
    }

    public static String[] unpackKeyId(String packed) {
        int idx = packed.indexOf(':');
        if (idx <= 0) throw new IllegalArgumentException("Invalid packed format");
        String keyId = packed.substring(0, idx);
        String base64 = packed.substring(idx + 1);
        return new String[]{ keyId, base64 };
    }
}
