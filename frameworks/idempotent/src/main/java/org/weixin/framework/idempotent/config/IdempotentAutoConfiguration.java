package org.weixin.framework.idempotent.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.cache.toolkit.RedisUtil;
import org.weixin.framework.idempotent.core.IdempotentAspect;


@RequiredArgsConstructor
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    private final IdempotentProperties idempotentProperties;

    @Bean
    public IdempotentAspect idempotentAspect(RedisUtil redisUtil) {
        return new IdempotentAspect(idempotentProperties, redisUtil);
    }

}
