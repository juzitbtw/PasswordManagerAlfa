package PasswordManager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyDeriver {
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] deriveKey(String password, byte[] salt, int iterations, int keyLength) throws Exception {
        if (salt == null) {
            throw new IllegalArgumentException("Salt must not be null");
        }

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength); // keyLength в БИТАХ
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded(); // Возвращаются байты (не биты)
    }
}