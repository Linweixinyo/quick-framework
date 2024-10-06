package org.weixin.framework.web.core.exception;

public class ServiceException extends AbstractException{

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable cause, IErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
