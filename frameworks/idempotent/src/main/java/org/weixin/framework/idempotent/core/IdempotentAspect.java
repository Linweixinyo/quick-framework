package org.weixin.framework.idempotent.core;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;
import org.weixin.framework.cache.toolkit.RedisDistributedCache;
import org.weixin.framework.idempotent.annotation.Idempotent;
import org.weixin.framework.idempotent.config.IdempotentProperties;
import org.weixin.framework.idempotent.toolkit.SpELUtil;
import org.weixin.framework.web.core.exception.ServiceException;
import org.weixin.framework.web.core.res.Result;
import org.weixin.framework.web.core.res.Results;
import org.weixin.framework.web.toolkit.ServletUtil;

import java.util.*;

@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

    public static final String IDEMPOTENT_KEY = "idempotent:paramKey";

    private final IdempotentProperties idempotentProperties;

    private final RedisDistributedCache redisDistributedCache;

    @Before("@annotation(idempotent)")
    public void checkHandler(JoinPoint joinPoint, Idempotent idempotent) {
        long interval = idempotent.timeUnit().toMillis(idempotent.interval());
        if (interval < 1000) {
            throw new ServiceException("idempotent interval must be greater than 1 second");
        }
        if (StrUtil.isNotBlank(idempotent.key())) {
            String key = (String) SpELUtil.parse(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
            setUnionKey(idempotent, key);
            return;
        }
        HttpServletRequest request = ServletUtil.getRequest();
        String requestURI = request.getRequestURI();
        String tokenValue = Optional.ofNullable(request.getHeader(idempotentProperties.getPrefix())).orElse(StrUtil.EMPTY);
        String paramStr = argsArrayToString(joinPoint.getArgs());
        String unionParam = SecureUtil.md5(String.join(StrUtil.COLON, tokenValue, paramStr));
        String redisKey = String.join(StrUtil.COLON, IDEMPOTENT_KEY, requestURI, unionParam);
        setUnionKey(idempotent, redisKey);
    }

    private void setUnionKey(Idempotent idempotent, String unionKey) {
        if (redisDistributedCache.setObjectIfAbsent(unionKey, StrUtil.EMPTY, idempotent.interval(), idempotent.timeUnit())) {
            IdempotentContext.setKey(unionKey);
        } else {
            throw new ServiceException(idempotent.message());
        }
    }

    @AfterReturning(pointcut = "@annotation(idempotent)", returning = "result")
    public void handlerAfterReturning(JoinPoint joinPoint, Idempotent idempotent, Object result) {
        if (result instanceof Result<?> r) {
            try {
                // 成功则不删除redis数据 保证在有效时间内无法重复提交
                if (Objects.equals(Results.SUCCESS_CODE, r.getCode())) {
                    return;
                }
                redisDistributedCache.delete(IdempotentContext.getKey());
            } finally {
                IdempotentContext.removeKey();
            }
        }
    }

    @AfterThrowing(value = "@annotation(idempotent)", throwing = "exception")
    public void handlerAfterThrowing(Idempotent idempotent, Exception exception) {
        redisDistributedCache.delete(IdempotentContext.getKey());
        IdempotentContext.removeKey();
    }


    private String argsArrayToString(Object[] paramsArray) {
        if (ArrayUtil.isEmpty(paramsArray)) {
            return StrUtil.SPACE;
        }
        List<String> params = new ArrayList<>();
        for (Object object : paramsArray) {
            if (Objects.nonNull(object) && !isFilterObject(object)) {
                params.add(JSONUtil.toJsonStr(object));
            }
        }
        return String.join(StrUtil.SPACE, params);
    }

    public boolean isFilterObject(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) object;
            return collection.stream().anyMatch(value -> value instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) object;
            return map.values().stream().anyMatch(value -> value instanceof MultipartFile);
        }
        return object instanceof MultipartFile || object instanceof ServletRequest || object instanceof ServletResponse;
    }


}
