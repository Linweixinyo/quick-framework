package org.weixin.framework.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {

    public static final String PREFIX = "framework.oss";

    /**
     * AccessKey
     */
    private String accessKey;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * Bucket的地域
     */
    private String regionName;

    /**
     * Bucket
     */
    private String bucketName;


    /**
     * 访问站点
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;


    /**
     * 服务提供商
     */
    private CloudServiceEnum cloudServiceEnum;


}
