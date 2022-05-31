package io.github.wendy512.easyboot.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 加解密工具类
 * 
 * @author taowenwu
 * @date 2021-04-09 15:26:15:26
 * @since 1.0.0
 */
public class EncryptionUtil {

    /**
     * SHA256加密
     *
     * @param rawPassword 密码
     *            
     * @param saltLength slat长度
     *                   
     * @param iterations 迭代次数
     *            
     * @return SHA256加密后的文本
     */
    public static String sha256(CharSequence rawPassword, int saltLength, int iterations) {
        try {
            return sha256(rawPassword, Hex.encodeHexString(getSalt(saltLength)), iterations);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha256(CharSequence rawPassword, CharSequence salt, int iterations) {
        String generatedPassword = null;
        String saltStr = salt.toString();

        try {

            byte[] saltBytes = Hex.decodeHex(salt.toString());
            MessageDigest md = DigestUtils.getDigest("SHA-256");
            // Add password bytes to digest
            md.update(saltBytes);
            // Get the hash's bytes
            byte[] source = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < source.length; i++) {
                sb.append(Integer.toString((source[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return saltStr + "." + generatedPassword;
    }

    public static byte[] getSalt(int saltLength) throws NoSuchAlgorithmException, NoSuchProviderException {
        // Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        // Create array for salt
        byte[] salt = new byte[saltLength];
        // Get a random salt
        sr.nextBytes(salt);
        // return salt
        return salt;
    }

    public static String hmacsha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }

        return sb.toString();
    }
}
