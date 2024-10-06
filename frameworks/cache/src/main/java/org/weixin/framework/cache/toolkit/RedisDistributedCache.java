package org.weixin.framework.cache.toolkit;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.weixin.framework.common.toolkit.jackson.JSONUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class RedisDistributedCache {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Boolean> LIMITED_COUNTER_SCRIPT;

    private static final DefaultRedisScript<Boolean> SLID_WINDOW_COUNTER;

    private static final String LUA_LIMITED_COUNTER_SCRIPT_PATH = "lua/limitedCounter.lua";

    private static final String LUA_SLID_WINDOW_COUNTER_SCRIPT_PATH = "lua/slidingWindowCounter.lua";

    static {
        // 计数器固定窗口算法
        LIMITED_COUNTER_SCRIPT = new DefaultRedisScript<>();
        LIMITED_COUNTER_SCRIPT.setLocation(new ClassPathResource(LUA_LIMITED_COUNTER_SCRIPT_PATH));
        LIMITED_COUNTER_SCRIPT.setResultType(Boolean.class);
        // 计数器滑动窗口算法
        SLID_WINDOW_COUNTER = new DefaultRedisScript<>();
        SLID_WINDOW_COUNTER.setLocation(new ClassPathResource(LUA_SLID_WINDOW_COUNTER_SCRIPT_PATH));
        SLID_WINDOW_COUNTER.setResultType(Boolean.class);

    }

    public <T> T loadAndSet(String key, Class<T> clazz, Supplier<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        if (exists(key)) {
            return get(key, clazz);
        }
        T result = cacheLoader.get();
        put(key, result, timeout, timeUnit);
        return result;
    }

    public <T> T loadAndSet(String key, TypeReference<T> typeReference, Supplier<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        if (exists(key)) {
            return get(key, typeReference);
        }
        T result = cacheLoader.get();
        put(key, result, timeout, timeUnit);
        return result;
    }

    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if(String.class.isAssignableFrom(clazz)) {
            return (T) value;
        }
        return JSONUtil.parseObject(value, clazz);
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONUtil.parseObject(value, typeReference);
    }

    public <T> List<T> getOfList(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONUtil.parseArray(value, clazz);
    }

    public Boolean put(String key, Object value) {
        if(!StrUtil.isBlank(key) || value == null) {
            return false;
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, actual);
        return true;
    }

    public Boolean put(String key, Object value, long timeout, TimeUnit timeUnit) {
        if(!StrUtil.isBlank(key) || value == null) {
            return false;
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, actual, timeout, timeUnit);
        return true;
    }

    public void delete(String... keys) {
        List<String> keyOfList = Arrays.stream(keys)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        stringRedisTemplate.delete(keyOfList);
    }

    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public StringRedisTemplate getInstance() {
        return stringRedisTemplate;
    }

}
