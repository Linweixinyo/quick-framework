package org.weixin.framework.encrypt.core.res;

import org.weixin.framework.encrypt.core.Encryptable;
import org.weixin.framework.web.core.res.Result;


public class EncryptResult<T> extends Result<T> implements Encryptable {


    public EncryptResult(Result<T> result) {
        super.setCode(result.getCode())
                .setMessage(result.getMessage())
                .setData(result.getData());
    }

    public static <T> EncryptResult<T> encryptResult(Result<T> result) {
        return new EncryptResult<>(result);
    }

}
