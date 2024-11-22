package org.weixin.framework.security.core.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.weixin.framework.web.core.res.Result;

@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleConstraintViolationException(HttpServletRequest request, AccessDeniedException ex) {
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), ex.toString());
        throw ex;
    }

    private String getUrl(HttpServletRequest request) {
        if (StringUtils.hasLength(request.getQueryString())) {
            return request.getRequestURL().toString() + "?" + request.getQueryString();
        }
        return request.getRequestURL().toString();
    }

}
