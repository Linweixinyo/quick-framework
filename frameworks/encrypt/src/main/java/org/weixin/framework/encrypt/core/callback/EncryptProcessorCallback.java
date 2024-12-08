package org.weixin.framework.encrypt.core.callback;

public interface EncryptProcessorCallback {

    /**
     * 解密前回调
     *
     * @param encryptData 加密数据
     * @param aesKey      AES密钥
     */
    default void decryptBefore(String encryptData, String aesKey) {

    }


    /**
     * 解密后回调
     *
     * @param requestData 请求数据
     */
    default void decryptAfter(Object requestData) {


    }


    /**
     * 加密前回调
     *
     * @param responseData 响应数据
     */
    default void encryptBefore(Object responseData) {

    }


    /**
     * 加密后回调
     *
     * @param encryptData 加密的响应数据
     */
    default void encryptAfter(String encryptData) {

    }

}
