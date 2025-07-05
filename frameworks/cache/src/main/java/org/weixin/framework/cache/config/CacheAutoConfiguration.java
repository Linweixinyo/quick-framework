package org.weixin.framework.cache.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.weixin.framework.cache.core.RedisKeySerializer;
import org.weixin.framework.cache.toolkit.RedisDistributedCache;

@RequiredArgsConstructor
@EnableConfigurationProperties(RedisCacheProperties.class)
public class CacheAutoConfiguration {

    private final RedisCacheProperties redisCacheProperties;

    @Bean
    public RedisKeySerializer redisKeySerializer() {
        String prefix = redisCacheProperties.getPrefix();
        String prefixCharset = redisCacheProperties.getPrefixCharset();
        return new RedisKeySerializer(prefix, prefixCharset);
    }


    @Bean
    public RedisDistributedCache redisDistributedCache(StringRedisTemplate stringRedisTemplate, RedisKeySerializer redisKeySerializer) {
        stringRedisTemplate.setKeySerializer(redisKeySerializer);
        return new RedisDistributedCache(stringRedisTemplate);
    }

}
