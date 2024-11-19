package org.weixin.framework.idempotent.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.cache.toolkit.RedisDistributedCache;
import org.weixin.framework.idempotent.core.IdempotentAspect;


@RequiredArgsConstructor
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    private final IdempotentProperties idempotentProperties;

    @Bean
    public IdempotentAspect idempotentAspect(RedisDistributedCache redisDistributedCache) {
        return new IdempotentAspect(idempotentProperties, redisDistributedCache);
    }

}
