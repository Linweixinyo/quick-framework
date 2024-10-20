package org.weixin.framework.web.toolkit;

import org.weixin.framework.web.core.exception.BaseErrorCode;
import org.weixin.framework.web.core.exception.IErrorCode;
import org.weixin.framework.web.core.exception.ServiceException;

import java.util.Objects;

public final class AssertUtil {

    public static void isTrue(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            throwException(errorCode);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throwException(message);
        }
    }

    public static void isFalse(boolean expression, IErrorCode errorCode) {
        if (expression) {
            throwException(errorCode);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throwException(message);
        }
    }

    public static void nonNull(Object obj, IErrorCode errorCode) {
        if (Objects.isNull(obj)) {
            throwException(errorCode);
        }
    }

    public static void nonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throwException(message);
        }
    }

    public static void equal(Object obj1, Object obj2, IErrorCode errorCode) {
        if (!Objects.equals(obj1, obj2)) {
            throwException(errorCode);
        }
    }

    public static void equal(Object obj1, Object obj2, String message) {
        if (!Objects.equals(obj1, obj2)) {
            throwException(message);
        }
    }


    private static void throwException(IErrorCode errorCode) {
        if (Objects.isNull(errorCode)) {
            errorCode = BaseErrorCode.SERVICE_ERROR;
        }
        throw new ServiceException(errorCode);
    }

    private static void throwException(String message) {
        if (Objects.isNull(message)) {
            message = BaseErrorCode.SERVICE_ERROR.message();
        }
        throw new ServiceException(message);
    }

}
