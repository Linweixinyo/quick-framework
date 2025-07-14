package org.weixin.framework.cache.core;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

public class CustomSpringCacheManager extends AbstractCacheManager {

    private final Map<String, Cache> cacheMap = new HashMap<>();

    private RedisTemplate<Object, Object> redisTemplate;

    public CustomSpringCacheManager(RedisTemplate<Object, Object> redisTemplate, String... cacheNames) {
        this.redisTemplate = redisTemplate;
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(List<String> cacheNames) {
        if (CollectionUtil.isEmpty(cacheNames)) {
            return;
        }
        for (String cacheName : cacheNames) {
            cacheMap.put(cacheName, new CustomSpringCache(true, cacheName, redisTemplate));
        }
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return cacheMap.values();
    }

    @Override
    protected Cache getMissingCache(String name) {
        return cacheMap.put(name, new CustomSpringCache(true, name, redisTemplate));
    }
}
