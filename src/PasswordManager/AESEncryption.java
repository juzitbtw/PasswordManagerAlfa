package PasswordManager;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class AESEncryption {
    private static final int GCM_TAG_LENGTH = 128; // 128 бит для HMAC
    private static final int IV_LENGTH = 12;       // 12 байт для IV

    public static String encrypt(byte[] encryptionKey, String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"), spec);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Создаём массив для хранения IV + зашифрованных данных
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);      // Копируем IV
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length); // Копируем данные

        return Base64.getEncoder().encodeToString(combined); // Кодируем в Base64
    }

    public static String decrypt(byte[] encryptionKey, String encryptedData) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        ByteBuffer buffer = ByteBuffer.wrap(decoded);

        byte[] iv = new byte[12];
        buffer.get(iv);
        byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"), spec);
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8); // Указываем UTF-8
    }
}