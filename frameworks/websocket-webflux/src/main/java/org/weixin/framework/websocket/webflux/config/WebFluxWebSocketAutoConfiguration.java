package org.weixin.framework.websocket.webflux.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.weixin.framework.websocket.webflux.core.handler.HearBeatExecutor;
import org.weixin.framework.websocket.webflux.core.handler.WebFluxWebSocketMessageExecutor;
import org.weixin.framework.websocket.webflux.core.handler.WebFluxWebSocketMessageHandler;
import org.weixin.framework.websocket.webflux.core.sender.WebFluxWebSocketMessageSender;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionManager;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionManagerImpl;

import java.util.List;

@EnableConfigurationProperties(WebFluxWebSocketProperties.class)
@ConditionalOnClass(WebSocketHandler.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(prefix = WebFluxWebSocketProperties.WEB_SOCKET_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebFluxWebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WebFluxWebSocketSessionManager.class)
    public WebFluxWebSocketSessionManager webFluxWebSocketSessionManager() {
        return new WebFluxWebSocketSessionManagerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(WebFluxWebSocketMessageSender.class)
    public WebFluxWebSocketMessageSender webFluxWebSocketMessageSender(WebFluxWebSocketSessionManager sessionManager) {
        return new WebFluxWebSocketMessageSender(sessionManager);
    }

    @Bean
    @ConditionalOnMissingBean(HearBeatExecutor.class)
    public HearBeatExecutor hearBeatExecutor(WebFluxWebSocketMessageSender webSocketMessageSender) {
        return new HearBeatExecutor(webSocketMessageSender);
    }

    @Bean
    public WebSocketHandler webFluxWebSocketHandler(WebFluxWebSocketSessionManager sessionManager,
                                                    List<? extends WebFluxWebSocketMessageExecutor<?>> executors) {
        return new WebFluxWebSocketMessageHandler(sessionManager, executors);
    }

}
