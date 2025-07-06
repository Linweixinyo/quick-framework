package org.weixin.framework.lock.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.weixin.framework.lock.core.LockTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class DistributeLock implements LockTemplate {

    private final RedissonClient redissonClient;

    @Override
    public boolean tryLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        return rLock.tryLock();
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(key);
        try {
            return rLock.tryLock(waitTime, timeUnit);
        } catch (InterruptedException e) {
            log.error("DistributeLock tryLock error key: {} message: {}", key, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long expire, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(key);
        try {
            return rLock.tryLock(waitTime, expire, timeUnit);
        } catch (InterruptedException e) {
            log.error("DistributeLock tryLock error key: {} message: {}", key, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }
}
