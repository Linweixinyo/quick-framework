package org.weixin.framework.encrypt.core.constant;

import java.util.concurrent.TimeUnit;

public interface EncryptConstant {

    String AES_KEY_ATTRIBUTE = "aes_key";

    String TIMESTAMP = "timestamp";

    String NONCE = "nonce";

    String SIGN = "sign";

    String DATA = "data";

    Integer REQUEST_TIME_OUT = 60;

    TimeUnit REQUEST_TIME_UNIT = TimeUnit.SECONDS;

    String SIGN_NONCE = "sign:nonce";

}
