package PasswordManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class IntegrityChecker {
    public static byte[] generateHMAC(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    public static boolean verifyHMAC(byte[] key, byte[] data, byte[] hmac) throws Exception {
        byte[] expectedHMAC = generateHMAC(key, data);
        return MessageDigest.isEqual(hmac, expectedHMAC);
    }
}