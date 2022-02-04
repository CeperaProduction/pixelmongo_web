package ru.pixelmongo.pixelmongo.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;

public class EncodeUtils {

    public static String md5(CharSequence input) {
        return md5(input, 1);
    }

    /**
     *
     * @param input - input for hashing
     * @param times - how much times operation MD5 hashing should repeat
     * @return
     */
    public static String md5(CharSequence input, int times) {
        if(times < 1) throw new IllegalArgumentException("Operations count can't be less than 1");
        MessageDigest encoder;
        try {
            encoder = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String hash = md5(encoder, input);
        for(int i = 1; i < times; i++) {
            encoder.reset();
            hash = md5(encoder, hash);
        }
        return hash;
    }

    private static String md5(MessageDigest encoder, CharSequence input) {
        byte[] bytes = input.toString().getBytes(StandardCharsets.UTF_8);
        encoder.update(bytes, 0, bytes.length);
        String hashedPass = new BigInteger(1, encoder.digest()).toString(16);
        while (hashedPass.length() < 32) {
           hashedPass = "0" + hashedPass;
        }
        return hashedPass;
    }

    public static String hmacSHA256(String key, String data) {
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

    public static String sha1(String input) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return new String(Hex.encode(sha1.digest(input.getBytes())));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

}
