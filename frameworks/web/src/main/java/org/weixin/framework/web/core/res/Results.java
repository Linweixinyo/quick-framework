package org.weixin.framework.web.core.res;

import org.weixin.framework.web.core.exception.AbstractException;
import org.weixin.framework.web.core.exception.BaseErrorCode;
import org.weixin.framework.web.core.exception.IErrorCode;

import java.util.Optional;

public final class Results {

    public static final String SUCCESS_CODE = "0";
    private static final String SUCCESS = "SUCCESS";

    /**
     * 成功响应
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(SUCCESS_CODE)
                .setMessage(SUCCESS);
    }

    /**
     * 携带数据的成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(SUCCESS_CODE)
                .setMessage(SUCCESS)
                .setData(data);
    }

    /**
     * 失败响应
     */
    public static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    public static Result<Void> failure(IErrorCode errorCode) {
        return new Result<Void>()
                .setCode(errorCode.code())
                .setMessage(errorCode.message());
    }

    /**
     * 通过异常构建失败响应
     */
    public static Result<Void> failure(AbstractException ex) {
        String errorCode = Optional.ofNullable(ex.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(ex.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    /**
     * 自定义失败响应
     */
    public static Result<Void> failure(String errorCode, String errorMessage) {
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

}
