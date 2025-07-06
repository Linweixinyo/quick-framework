package org.weixin.framework.lock.core;

import java.util.concurrent.TimeUnit;

public interface LockTemplate {

    boolean tryLock(String key);

    boolean tryLock(String key, long waitTime, TimeUnit timeUnit);

    boolean tryLock(String key, long waitTime, long expire, TimeUnit timeUnit);

    void unlock(String key);

}