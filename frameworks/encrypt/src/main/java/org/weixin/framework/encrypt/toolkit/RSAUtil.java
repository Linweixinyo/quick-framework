package org.weixin.framework.encrypt.toolkit;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    public static final String ALGORITHM = "RSA";

    /**
     * 获取公钥和私钥
     *
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 公钥加密
     *
     * @param content         原文
     * @param base64PublicKey 公钥
     * @return 公钥加密结果
     */
    public static String encrypt(String content, String base64PublicKey) {
        try {
            PublicKey publicKey = getPublicKeyFromString(base64PublicKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 私钥解密
     *
     * @param content          base64字符串（加密获得）
     * @param base64PrivateKey 私钥
     * @return 私钥解密结果
     */
    public static String decrypt(String content, String base64PrivateKey) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(base64PrivateKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decode = Base64.decodeBase64(content);
            byte[] result = cipher.doFinal(decode);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 转换公钥
     *
     * @param base64PublicKey 公钥（Base64编码）
     * @return 公钥
     */
    public static PublicKey getPublicKeyFromString(String base64PublicKey) {
        try {
            byte[] decode = Base64.decodeBase64(base64PublicKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 转换私钥
     *
     * @param base64PrivateKey 私钥（Base64编码）
     * @return 私钥
     */
    public static PrivateKey getPrivateKeyFromString(String base64PrivateKey) {
        try {
            byte[] decode = Base64.decodeBase64(base64PrivateKey);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        KeyPair keyPair = generateKeyPair();
        System.out.println(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
        System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
    }

}
