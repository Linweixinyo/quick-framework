package org.weixin.framework.webflux.core.exception;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.weixin.framework.common.web.core.exception.AbstractException;
import org.weixin.framework.common.web.core.exception.BaseErrorCode;
import org.weixin.framework.common.web.core.res.Result;
import org.weixin.framework.common.web.core.res.Results;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class WebFluxGlobalExceptionHandler {

    /**
     * 拦截请求参数校验异常
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleWebExchangeBindException(ServerWebExchange exchange, WebExchangeBindException ex) {
        FieldError firstFieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        log.error("[{}] {} [ex] {}", exchange.getRequest().getMethod(), getUrl(exchange), exceptionStr);
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 拦截请求参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ServerWebExchange exchange, ConstraintViolationException ex) {
        log.error("[{}] {} [ex] {}", exchange.getRequest().getMethod(), getUrl(exchange), ex.toString());
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), ex.getMessage());
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(AbstractException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleAbstractException(ServerWebExchange exchange, AbstractException ex) {
        if (ex.getCause() != null) {
            log.error("[{}] {} [ex] {}", exchange.getRequest().getMethod(), getUrl(exchange), ex.toString(), ex.getCause());
            return Results.failure(ex);
        }
        log.error("[{}] {} [ex] {}", exchange.getRequest().getMethod(), getUrl(exchange), ex.toString());
        return Results.failure(ex);
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleThrowable(ServerWebExchange exchange, Throwable throwable) {
        log.error("[{}] {} ", exchange.getRequest().getMethod(), getUrl(exchange), throwable);
        return Results.failure();
    }

    private String getUrl(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        String query = exchange.getRequest().getURI().getQuery();
        if (StrUtil.isNotBlank(query)) {
            return path + "?" + query;
        }
        return path;
    }
}
