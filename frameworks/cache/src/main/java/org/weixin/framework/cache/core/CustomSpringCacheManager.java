package org.weixin.framework.cache.core;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.weixin.framework.cache.toolkit.RedisUtil;

import java.util.*;

public class CustomSpringCacheManager extends AbstractCacheManager {

    private final Map<String, Cache> cacheMap = new HashMap<>();

    private final RedisUtil redisUtil;

    public CustomSpringCacheManager(RedisUtil redisUtil, String... cacheNames) {
        this.redisUtil = redisUtil;
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(List<String> cacheNames) {
        if (CollectionUtil.isEmpty(cacheNames)) {
            return;
        }
        for (String cacheName : cacheNames) {
            cacheMap.put(cacheName, new CustomSpringCache(true, cacheName, redisUtil));
        }
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return cacheMap.values();
    }

    @Override
    protected Cache getMissingCache(String name) {
        CustomSpringCache customSpringCache = new CustomSpringCache(true, name, redisUtil);
        cacheMap.put(name, customSpringCache);
        return customSpringCache;
    }
}
