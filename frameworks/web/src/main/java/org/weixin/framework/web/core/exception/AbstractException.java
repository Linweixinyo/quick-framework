package org.weixin.framework.web.core.exception;


import cn.hutool.core.util.StrUtil;

import java.util.Optional;


public class AbstractException extends RuntimeException {

    private final String errorCode;

    private final String errorMessage;


    public AbstractException(String message, Throwable cause, IErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(message)
                .filter(StrUtil::isNotBlank)
                .orElse(errorCode.message());
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
