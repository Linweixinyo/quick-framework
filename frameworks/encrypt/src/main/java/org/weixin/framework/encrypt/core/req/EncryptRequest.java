package org.weixin.framework.encrypt.core.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EncryptRequest {

    /**
     * 请求数据-AES加密
     */
    private String data;


    /**
     * AES密钥
     */
    private String aesKey;


    /**
     * 签名
     */
    private String sign;

}
