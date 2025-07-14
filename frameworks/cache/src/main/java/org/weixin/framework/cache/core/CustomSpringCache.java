package org.weixin.framework.cache.core;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Objects;
import java.util.concurrent.Callable;

public class CustomSpringCache extends AbstractValueAdaptingCache {

    private final String name;

    private final RedisTemplate<Object, Object> redisTemplate;

    public CustomSpringCache(boolean allowNullValues, String name, RedisTemplate<Object, Object> redisTemplate) {
        super(allowNullValues);
        this.name = name;
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected Object lookup(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ValueOperations<Object, Object> getNativeCache() {
        return redisTemplate.opsForValue();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(value)) {
            try {
                value = (T) valueLoader.call();
            } catch (Exception ex) {
                throw new Cache.ValueRetrievalException(key, valueLoader, ex);
            }
            redisTemplate.opsForValue().set(key, value);
        }
        return (T) fromStoreValue(value);
    }

    @Override
    public void put(Object key, Object value) {
        redisTemplate.opsForValue().set(key, toStoreValue(value));
    }

    @Override
    public void evict(Object key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clear() {
        redisTemplate.delete("*");
    }
}
