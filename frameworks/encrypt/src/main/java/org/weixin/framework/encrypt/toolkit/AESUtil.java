package org.weixin.framework.encrypt.toolkit;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public final class AESUtil {

    public static final String ALGORITHM = "AES";

    public static final String MODE = "AES/ECB/PKCS5Padding";


    /**
     * AES加密
     *
     * @param content 原文
     * @param key     密钥
     * @return 加密结果（Base64转换）
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher encipher = Cipher.getInstance(MODE);
            encipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] result = encipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * AES解密
     *
     * @param content base64字符串（加密获得）
     * @param key     密钥
     * @return 解密结果
     */
    public static String decrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher encipher = Cipher.getInstance(MODE);
            encipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decode = Base64.decodeBase64(content);
            byte[] result = encipher.doFinal(decode);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public static void main(String[] args) {
        String content = "linweixin";
        String key = "chenpeiyiloveyou";
        String encryptStr = encrypt(content, key);
        System.out.println(encryptStr);
        String decryptStr = decrypt(encryptStr, key);
        System.out.println(decryptStr);

    }


}
