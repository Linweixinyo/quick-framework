package org.weixin.framework.websocket.webflux.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = WebFluxWebSocketProperties.WEB_SOCKET_PREFIX)
public class WebFluxWebSocketProperties {

    public static final String WEB_SOCKET_PREFIX = "framework.websocket.webflux.config";

    private Boolean enabled;

}
