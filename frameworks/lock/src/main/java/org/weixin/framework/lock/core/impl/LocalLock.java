package org.weixin.framework.lock.core.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.weixin.framework.lock.core.LockTemplate;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LocalLock implements LockTemplate {

    private static final Cache<String, ReentrantLock> LOCK_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();


    @Override
    public boolean tryLock(String key) {
        boolean locked = false;
        try {
            ReentrantLock reentrantLock = LOCK_CACHE.get(key, ReentrantLock::new);
            locked = reentrantLock.tryLock();
        } catch (ExecutionException e) {
            log.error("LocalLock tryLock error key: {} message: {}", key, e.getMessage(), e);
        }
        return locked;
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit timeUnit) {
        boolean locked = false;
        try {
            ReentrantLock reentrantLock = LOCK_CACHE.get(key, ReentrantLock::new);
            locked = reentrantLock.tryLock(waitTime, timeUnit);
        } catch (ExecutionException | InterruptedException e) {
            log.error("LocalLock tryLock error key: {} message: {}", key, e.getMessage(), e);
        }
        return locked;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long expire, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("LocalLock not supported expire");
    }

    @Override
    public void unlock(String key) {
        ReentrantLock reentrantLock = LOCK_CACHE.getIfPresent(key);
        if (Objects.nonNull(reentrantLock) && reentrantLock.isLocked() && reentrantLock.isHeldByCurrentThread()) {
            reentrantLock.unlock();
        }
    }
}
