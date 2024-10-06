package org.weixin.framework.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = RedisCacheProperties.PREFIX)
public class RedisCacheProperties {

    public static final String PREFIX = "framework.cache.redis";

    /**
     * Key 前缀
     */
    private String prefix = "";

    /**
     * Key 前缀字符集
     */
    private String prefixCharset = "UTF-8";

    /**
     * Value 过期时间
     */
    private Long valueTimeout = 30000L;

    /**
     * Value 过期时间单位
     */
    private TimeUnit valueTimeUnit = TimeUnit.MILLISECONDS;



}
