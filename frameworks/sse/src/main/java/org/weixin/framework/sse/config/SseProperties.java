package org.weixin.framework.sse.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SseProperties.SSE_PREFIX)
public class SseProperties {

    public static final String SSE_PREFIX = "framework.sse.config";


    private Boolean enabled;

}
