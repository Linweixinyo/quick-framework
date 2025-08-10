package org.weixin.framework.cache.toolkit;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.weixin.framework.cache.core.CacheOperationResult;
import org.weixin.framework.cache.core.CacheStatus;
import org.weixin.framework.common.toolkit.jackson.JSONUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public final class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public static final DefaultRedisScript<Boolean> LIMITED_COUNTER_SCRIPT;

    public static final DefaultRedisScript<Boolean> SLID_WINDOW_COUNTER;

    // 缓存相关的Lua脚本
    public static final DefaultRedisScript<List> GET_CACHE_SCRIPT;

    public static final DefaultRedisScript<List> INVALID_CACHE_SCRIPT;

    public static final DefaultRedisScript<Void> SET_CACHE_SCRIPT;

    private static final String LUA_LIMITED_COUNTER_SCRIPT_PATH = "lua/limitedCounter.lua";

    private static final String LUA_SLID_WINDOW_COUNTER_SCRIPT_PATH = "lua/slidingWindowCounter.lua";

    // 缓存脚本路径
    private static final String LUA_GET_CACHE_SCRIPT_PATH = "lua/GetCache.lua";

    private static final String LUA_INVALID_CACHE_SCRIPT_PATH = "lua/InvalidCache.lua";

    private static final String LUA_SET_CACHE_SCRIPT_PATH = "lua/SetCache.lua";

    static {
        // 计数器固定窗口算法
        LIMITED_COUNTER_SCRIPT = new DefaultRedisScript<>();
        LIMITED_COUNTER_SCRIPT.setLocation(new ClassPathResource(LUA_LIMITED_COUNTER_SCRIPT_PATH));
        LIMITED_COUNTER_SCRIPT.setResultType(Boolean.class);
        // 计数器滑动窗口算法
        SLID_WINDOW_COUNTER = new DefaultRedisScript<>();
        SLID_WINDOW_COUNTER.setLocation(new ClassPathResource(LUA_SLID_WINDOW_COUNTER_SCRIPT_PATH));
        SLID_WINDOW_COUNTER.setResultType(Boolean.class);

        // 初始化缓存相关脚本
        GET_CACHE_SCRIPT = new DefaultRedisScript<>();
        GET_CACHE_SCRIPT.setLocation(new ClassPathResource(LUA_GET_CACHE_SCRIPT_PATH));
        GET_CACHE_SCRIPT.setResultType(List.class);

        INVALID_CACHE_SCRIPT = new DefaultRedisScript<>();
        INVALID_CACHE_SCRIPT.setLocation(new ClassPathResource(LUA_INVALID_CACHE_SCRIPT_PATH));
        INVALID_CACHE_SCRIPT.setResultType(List.class);

        SET_CACHE_SCRIPT = new DefaultRedisScript<>();
        SET_CACHE_SCRIPT.setLocation(new ClassPathResource(LUA_SET_CACHE_SCRIPT_PATH));
        SET_CACHE_SCRIPT.setResultType(Void.class);

    }

    public <T> T loadAndSet(String key, Type type, Supplier<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        if (exists(key)) {
            return get(key, type);
        }
        T result = cacheLoader.get();
        put(key, result, timeout, timeUnit);
        return result;
    }

    public Boolean setObjectIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        Class<?> clazz = value.getClass();
        String valueStr = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        return stringRedisTemplate.opsForValue().setIfAbsent(key, valueStr, timeout, timeUnit);
    }

    public String get(String key) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Type type) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if (type instanceof Class<?> clazz && String.class.equals(clazz)) {
            return (T) value;
        }
        return JSONUtil.parseObject(value, type);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(value)) {
            return Collections.emptyList();
        }
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

    public <T> T hashGet(String key, String hashkey, Type type) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be blank");
        }
        if (StrUtil.isBlank(hashkey)) {
            throw new IllegalArgumentException("HashKey cannot be null");
        }
        Object value = stringRedisTemplate.opsForHash().get(key, hashkey);
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            return JSONUtil.parseObject((String) value, type);
        }
        return (T) value;
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


    /**
     * Spring Cache适配方法 - 获取缓存
     * 简化的获取缓存方法，使用默认参数
     *
     * @param key 缓存键
     * @return 缓存操作结果
     */
    public CacheOperationResult getCache(String owner, String key) {
        return getCacheWithLua(key, 30000L, owner); // 默认30秒锁超时
    }

    /**
     * Spring Cache适配方法 - 设置缓存
     * 简化的设置缓存方法，使用默认参数
     *
     * @param key           缓存键
     * @param value         缓存值
     * @param expireSeconds 过期时间(秒)
     * @return 缓存操作结果
     */
    public CacheOperationResult setCache(String owner, String key, Object value, long expireSeconds) {
        return setCacheWithLua(key, value, owner, expireSeconds);
    }

    /**
     * Spring Cache适配方法 - 失效缓存
     * 简化的失效缓存方法
     *
     * @param key 缓存键
     * @return 缓存操作结果
     */
    public CacheOperationResult invalidCache(String key) {
        return invalidCacheWithLua(key, System.currentTimeMillis());
    }


    /**
     * 使用Lua脚本获取缓存
     * 支持分布式锁机制，防止缓存击穿
     *
     * @param key           缓存键
     * @param lockTimeoutMs 锁超时时间(毫秒)
     * @param owner         锁拥有者标识
     * @return 缓存操作结果
     */
    private CacheOperationResult getCacheWithLua(String key, long lockTimeoutMs, String owner) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Cache key cannot be blank");
        }
        if (StrUtil.isBlank(owner)) {
            throw new IllegalArgumentException("Lock owner cannot be blank");
        }

        long currentTime = System.currentTimeMillis();
        long newUnlockTime = currentTime + lockTimeoutMs;

        try {
            List result = stringRedisTemplate.execute(GET_CACHE_SCRIPT, List.of(key), String.valueOf(newUnlockTime), owner, String.valueOf(currentTime));

            if (result != null && result.size() >= 2) {
                Object value = result.get(0);
                String status = result.get(1) != null ? result.get(1).toString() : CacheStatus.NEED_QUERY.getCode();

                return new CacheOperationResult(value, status);
            }

            return new CacheOperationResult(null, CacheStatus.NEED_QUERY);
        } catch (Exception e) {
            // 日志记录异常
            log.error("getCacheWithLua error: {}", e.getMessage(), e);
            return new CacheOperationResult(null, CacheStatus.UNKNOWN);
        }
    }

    /**
     * 使用Lua脚本失效缓存
     * 设置缓存为锁定状态，阻止读请求
     *
     * @param key          缓存键
     * @param unlockTimeMs 解锁时间(毫秒)，0表示不设置过期时间
     * @return 缓存操作结果
     */
    private CacheOperationResult invalidCacheWithLua(String key, long unlockTimeMs) {
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Cache key cannot be blank");
        }

        try {
            List result = stringRedisTemplate.execute(INVALID_CACHE_SCRIPT, List.of(key), String.valueOf(unlockTimeMs));

            if (result != null && result.size() >= 2) {
                Object value = result.get(0);
                String status = result.get(1) != null ? result.get(1).toString() : CacheStatus.INVALID_FAILED.getCode();

                return new CacheOperationResult(value, status);
            }

            return new CacheOperationResult(null, CacheStatus.INVALID_FAILED);
        } catch (Exception e) {
            // 日志记录异常
            log.error("invalidCacheWithLua error: {}", e.getMessage(), e);
            return new CacheOperationResult(null, CacheStatus.INVALID_FAILED);
        }
    }

    /**
     * 使用Lua脚本设置缓存
     * 验证锁拥有者后设置缓存值并释放锁
     *
     * @param key           缓存键
     * @param value         缓存值
     * @param owner         锁拥有者标识
     * @param expireSeconds 缓存过期时间(秒)
     * @return 缓存操作结果
     */
    private CacheOperationResult setCacheWithLua(String key, Object value, String owner, long expireSeconds) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Cache value cannot be null");
        }
        if (StrUtil.isBlank(owner)) {
            throw new IllegalArgumentException("Lock owner cannot be blank");
        }

        try {
            String valueStr = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
            stringRedisTemplate.execute(SET_CACHE_SCRIPT, List.of(key), valueStr, owner, String.valueOf(expireSeconds));

            return new CacheOperationResult(value, CacheStatus.SET_SUCCESS);
        } catch (Exception e) {
            // 日志记录异常
            log.error("setCacheWithLua error: {}", e.getMessage(), e);
            return new CacheOperationResult(null, CacheStatus.SET_FAILED);
        }
    }

}
