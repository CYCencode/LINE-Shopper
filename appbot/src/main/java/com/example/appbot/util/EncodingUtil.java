package com.example.appbot.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Component
@Log4j2
public class EncodingUtil {
    // create SecretKey
    public static SecretKeySpec getSecretKey(String hashKey) {
        byte[] keyBytes = hashKey.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }

    // create IvParameterSpec
    public static IvParameterSpec getIvParameterSpec(String hashIV) {
        byte[] ivBytes = hashIV.getBytes();
        return new IvParameterSpec(ivBytes);
    }

    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    public static String getURLEncoding(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
    public static String getURLDecoding(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    public static String getCalculateCMV(Map<String, ?> map, String hashKey, String hashIV) {
        // url params
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey").append("=").append(hashKey).append("&");
        for(String key : map.keySet()) {
            sb.append(key).append("=").append(map.get(key)).append("&");
        }
        sb.append("HashIV").append("=").append(hashIV).append("&");
        sb.deleteCharAt(sb.length() - 1);

        // urlencoding
        String encText = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8).toLowerCase();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(encText.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                // use & 0xff to get unsigned value
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 Algorithm not found.", e);
        }
    }
}
