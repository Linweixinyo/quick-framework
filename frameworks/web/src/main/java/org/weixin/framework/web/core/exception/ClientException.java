package org.weixin.framework.web.core.exception;

public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable cause, IErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
