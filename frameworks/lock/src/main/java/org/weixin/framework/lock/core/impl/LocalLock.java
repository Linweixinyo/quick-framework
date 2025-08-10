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
        // 注意：不要使用getIfPresent
        ReentrantLock reentrantLock = null;
        try {
            reentrantLock = LOCK_CACHE.get(key, () -> {
                log.error("unlock error lock key: {}", key);
                throw new RuntimeException("unlock error lock key: " + key);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(reentrantLock) && reentrantLock.isLocked() && reentrantLock.isHeldByCurrentThread()) {
            reentrantLock.unlock();
            log.info("unlock success lock key: {} lock message: holdCount {}", key, reentrantLock.getHoldCount());
        }
    }

    public static int result = 0;

    public static void main(String[] args) throws InterruptedException {
        LocalLock localLock = new LocalLock();
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                boolean locked = localLock.tryLock("test", 1, TimeUnit.MINUTES);
                if (!locked) {
                    continue;
                }
                try {
                    result++;
                } finally {
                    localLock.unlock("test");
                }
            }
        });
        thread.start();
        for (int i = 0; i < 10000; i++) {
            boolean locked = localLock.tryLock("test", 1, TimeUnit.MINUTES);
            if (!locked) {
                continue;
            }
            try {
                result++;
            } finally {
                localLock.unlock("test");
            }
        }
        thread.join();
        System.out.println(result);
    }
}
