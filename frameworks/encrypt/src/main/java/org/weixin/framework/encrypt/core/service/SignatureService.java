package org.weixin.framework.encrypt.core.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.weixin.framework.encrypt.core.constant.EncryptConstant;
import org.weixin.framework.web.toolkit.ServletUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SignatureService {

    private final StringRedisTemplate stringRedisTemplate;


    public boolean checkHeader(HttpServletRequest request) {
        String timestamp = request.getHeader(EncryptConstant.TIMESTAMP);
        if (StrUtil.isBlank(timestamp)) {
            return false;
        }
        String nonce = request.getHeader(EncryptConstant.NONCE);
        if (StrUtil.isBlank(nonce) || nonce.length() < 10) {
            return false;
        }
        String sign = request.getHeader(EncryptConstant.SIGN);
        if (StrUtil.isBlank(sign)) {
            return false;
        }

        long intervalTime = EncryptConstant.REQUEST_TIME_UNIT.toMillis(EncryptConstant.REQUEST_TIME_OUT);
        long requestTimestamp = Long.parseLong(timestamp);
        if (Math.abs(System.currentTimeMillis() - requestTimestamp) > intervalTime) {
            return false;
        }
        setNonce(nonce, intervalTime * 2, EncryptConstant.REQUEST_TIME_UNIT);
        return !checkNonceExist(nonce);
    }

    public String buildSignature(String encryptData) {
        HttpServletRequest request = ServletUtil.getRequest();
        if (!checkHeader(request)) {
            throw new IllegalArgumentException("Signature parameters is error");
        }
        String timestamp = request.getHeader(EncryptConstant.TIMESTAMP);
        String nonce = request.getHeader(EncryptConstant.NONCE);
        Map<String, String> parameterMap = Map.of(EncryptConstant.TIMESTAMP, timestamp, EncryptConstant.NONCE, nonce, EncryptConstant.DATA, encryptData);
        String sign = MapUtil.join(parameterMap, "&", "=");
        return new String(DigestUtils.md5(sign), StandardCharsets.UTF_8);
    }


    public void setNonce(String nonce, long timeout, TimeUnit timeUnit) {
        String redisKey = buildKey(EncryptConstant.SIGN_NONCE, nonce);
        stringRedisTemplate.opsForValue().setIfAbsent(redisKey, StrUtil.EMPTY, timeout, timeUnit);
    }

    public boolean checkNonceExist(String nonce) {
        String redisKey = buildKey(EncryptConstant.SIGN_NONCE, nonce);
        return stringRedisTemplate.hasKey(redisKey);
    }


    public static String buildKey(String... keys) {
        for (String key : keys) {
            if (StrUtil.isBlank(key)) {
                throw new IllegalArgumentException("构建缓存 key 不允许为空");
            }
        }
        return String.join(StrUtil.COLON, keys);
    }
}
