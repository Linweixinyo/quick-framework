package org.weixin.framework.cache.toolkit;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class RedisDistributedLock {

    private final RedissonClient redissonClient;


    public void lock(String key, Runnable runnable, Long expire, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;
        try {
            locked = lock.tryLock(expire, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!locked) throw new RuntimeException("请稍后再操作");
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }


    }

}
