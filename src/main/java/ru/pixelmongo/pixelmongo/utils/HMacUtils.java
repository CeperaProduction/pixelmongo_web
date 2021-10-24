package ru.pixelmongo.pixelmongo.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;

public class HMacUtils {

    public static String sha256(String key, String data) {
        if (key == null || data == null) throw new NullPointerException();
        String algName = "HmacSHA256";
        try {
            Mac hmac = Mac.getInstance(algName);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, algName);
            hmac.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] res = hmac.doFinal(dataBytes);
            return new String(Hex.encode(res));
        }catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
