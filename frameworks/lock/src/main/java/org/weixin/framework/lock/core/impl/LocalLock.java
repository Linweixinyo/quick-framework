package org.weixin.framework.lock.core.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.weixin.framework.lock.core.LockTemplate;

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
            log.error("LocalLock tryLock error, key: {}", key, e);
        }
        return locked;
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit timeUnit) {
        boolean locked = false;
        try {
            ReentrantLock reentrantLock = LOCK_CACHE.get(key, ReentrantLock::new);
            locked = reentrantLock.tryLock(waitTime, timeUnit);
        } catch (ExecutionException e) {
            log.error("LocalLock tryLock error, key: {}", key, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("LocalLock tryLock interrupted, key: {}", key, e);
        }
        return locked;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long expire, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("LocalLock not supported expire");
    }

    @Override
    public void unlock(String key) {
        ReentrantLock reentrantLock;
        try {
            reentrantLock = LOCK_CACHE.get(key, () -> {
                throw new IllegalStateException("unlock error, lock not found, key: " + key);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (reentrantLock.isHeldByCurrentThread()) {
            reentrantLock.unlock();
        }
    }
}
