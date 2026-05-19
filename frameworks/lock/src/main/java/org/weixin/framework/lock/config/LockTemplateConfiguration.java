package org.weixin.framework.lock.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.weixin.framework.lock.core.impl.DistributeLock;
import org.weixin.framework.lock.core.impl.LocalLock;

public class LockTemplateConfiguration {


    @Configuration
    @ConditionalOnClass(RedissonClient.class)
    static class DistributeLockConfiguration {

        @Bean
        @ConditionalOnBean(RedissonClient.class)
        public DistributeLock distributeLock(RedissonClient redissonClient) {
            return new DistributeLock(redissonClient);
        }

    }


    @Bean
    public LocalLock localLock() {
        return new LocalLock();
    }


}
