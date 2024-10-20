package org.weixin.framework.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = WebSocketProperties.WEB_SOCKET_PREFIX)
public class WebSocketProperties {

    public static final String WEB_SOCKET_PREFIX = "framework.websocket.config";


    private Boolean enabled;

}
