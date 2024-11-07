package org.weixin.framework.security.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(prefix = SecurityProperties.SECURITY_PREFIX)
public class SecurityProperties {

    public static final String SECURITY_PREFIX = "framework.security.config";


    /**
     * 从请求头中获取令牌
     */
    private String header = "Authorization";


    /**
     * 从请求参数中获取令牌，用于WebSocket
     */
    private String parameter = "token";

    /**
     * Token前缀
     */
    private String tokenPrefix = "Bearer";


    /**
     * 放行路径
     */
    private List<String> ignoreUrls = Collections.emptyList();

}
