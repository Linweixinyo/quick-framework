package org.weixin.framework.cache.toolkit;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.weixin.framework.common.toolkit.jackson.JSONUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Boolean setObjectIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        Class<?> clazz = value.getClass();
        String valueStr;
        if (String.class.isAssignableFrom(clazz)) {
            valueStr = (String) value;
        } else {
            valueStr = JSONUtil.toJsonStr(value);
        }
        return stringRedisTemplate.opsForValue().setIfAbsent(key, valueStr, timeout, timeUnit);
    }


    public String get(String key) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        if (String.class.isAssignableFrom(clazz)) {
            return (T) value;
        }
        return JSONUtil.parseObject(value, clazz);
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONUtil.parseObject(value, typeReference);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONUtil.parseArray(value, clazz);
    }

    public void put(String key, Object value) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, actual);
    }

    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, actual, timeout, timeUnit);
    }

    public String hashGet(String key, String hashkey) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (StrUtil.isBlank(hashkey)) {
            throw new IllegalArgumentException("HashKey cannot be null");
        }
        Object value = stringRedisTemplate.opsForHash().get(key, hashkey);
        return value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
    }


    public <T> T hashGet(String key, String hashkey, Class<T> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (StrUtil.isBlank(hashkey)) {
            throw new IllegalArgumentException("HashKey cannot be null");
        }
        Object value = stringRedisTemplate.opsForHash().get(key, hashkey);
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        return JSONUtil.parseObject(actual, clazz);
    }

    public <T> T hashGet(String key, String hashkey, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (StrUtil.isBlank(hashkey)) {
            throw new IllegalArgumentException("HashKey cannot be null");
        }
        Object value = stringRedisTemplate.opsForHash().get(key, hashkey);
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        return JSONUtil.parseObject(actual, typeReference);
    }


    public void hashPut(String key, String hashKey, Object value) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForHash().put(key, hashKey, actual);
    }

    public void hashPut(String key, String hashKey, Object value, long timeout, TimeUnit timeUnit) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        String actual = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        stringRedisTemplate.expire(key, timeout, timeUnit);
        stringRedisTemplate.opsForHash().put(key, hashKey, actual);
    }

    public void hashPut(String key, Map<String, Object> mapValue) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (CollectionUtil.isEmpty(mapValue)) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        Map<String, String> valueMap = new HashMap<>();
        mapValue.forEach((hashKey, value) -> {
            valueMap.put(hashKey, value instanceof String ? (String) value : JSONUtil.toJsonStr(value));
        });
        stringRedisTemplate.opsForHash().putAll(key, valueMap);
    }

    public void hashPut(String key, Map<String, Object> mapValue, long timeout, TimeUnit timeUnit) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (CollectionUtil.isEmpty(mapValue)) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        Map<String, String> valueMap = new HashMap<>();
        mapValue.forEach((hashKey, value) -> {
            valueMap.put(hashKey, value instanceof String ? (String) value : JSONUtil.toJsonStr(value));
        });
        stringRedisTemplate.expire(key, timeout, timeUnit);
        stringRedisTemplate.opsForHash().putAll(key, valueMap);
    }

    public Boolean hashExists(String key, String hashKey) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (StrUtil.isBlank(hashKey)) {
            throw new IllegalArgumentException("HashKey cannot be null");
        }
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public void delete(String... keys) {
        List<String> keyList = Arrays.stream(keys)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        stringRedisTemplate.delete(keyList);
    }

    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public StringRedisTemplate getInstance() {
        return stringRedisTemplate;
    }

}
