package org.weixin.framework.cache.config;


import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.weixin.framework.cache.core.CustomSpringCacheManager;
import org.weixin.framework.cache.core.RedisKeySerializer;
import org.weixin.framework.cache.toolkit.RedisUtil;

import java.lang.reflect.Method;


@EnableCaching
@EnableMethodCache(basePackages = "org.weixin.framework")
@AutoConfigureBefore(org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class)
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
    public RedisUtil redisUtil(RedisTemplate<Object, Object> redisTemplate,
                               StringRedisTemplate stringRedisTemplate,
                               RedisKeySerializer redisKeySerializer) {
        redisTemplate.setKeySerializer(redisKeySerializer);
        redisTemplate.setValueSerializer(RedisSerializer.json());
        stringRedisTemplate.setKeySerializer(redisKeySerializer);
        return new RedisUtil(stringRedisTemplate);
    }

    @RequiredArgsConstructor
    static class CustomCachingConfigurer implements CachingConfigurer {

        private final RedisTemplate<Object, Object> redisTemplate;

        @Override
        public CacheManager cacheManager() {
            return new CustomSpringCacheManager(redisTemplate);
        }

        @Override
        public KeyGenerator keyGenerator() {
            return new KeyGenerator() {
                @Override
                public Object generate(Object target, Method method, Object... params) {
                    return "spring:cache:" + method.getName();
                }
            };
        }
    }

    @Bean
    public CachingConfigurer cachingConfigurer(RedisTemplate<Object, Object> redisTemplate) {
        return new CustomCachingConfigurer(redisTemplate);
    }

}
