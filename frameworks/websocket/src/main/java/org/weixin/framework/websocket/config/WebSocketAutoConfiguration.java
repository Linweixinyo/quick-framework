package org.weixin.framework.websocket.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.weixin.framework.websocket.core.handler.HearBeatExecutor;
import org.weixin.framework.websocket.core.handler.WebSocketMessageExecutor;
import org.weixin.framework.websocket.core.handler.WebSocketMessageHandler;
import org.weixin.framework.websocket.core.sender.WebSocketMessageSender;
import org.weixin.framework.websocket.core.session.WebSocketSessionDecorator;
import org.weixin.framework.websocket.core.session.WebSocketSessionManager;
import org.weixin.framework.websocket.core.session.WebSocketSessionManagerImpl;

import java.util.List;


@EnableWebSocket
@RequiredArgsConstructor
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(prefix = WebSocketProperties.WEB_SOCKET_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WebSocketSessionManager.class)
    public WebSocketSessionManager webSocketSessionManager() {
        return new WebSocketSessionManagerImpl();
    }

    @Bean
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager webSocketSessionManager) {
        return new WebSocketMessageSender(webSocketSessionManager);
    }

    @Bean
    public WebSocketHandler webSocketHandler(ObjectProvider<WebSocketSessionDecorator> webSocketSessionDecoratorProvider,
                                             WebSocketSessionManager sessionManager,
                                             List<? extends WebSocketMessageExecutor> executors) {
        WebSocketMessageHandler webSocketMessageHandler = new WebSocketMessageHandler(executors);
        return webSocketSessionDecoratorProvider.getIfAvailable(() -> new WebSocketSessionDecorator(webSocketMessageHandler, sessionManager));
    }

    @Bean
    public HearBeatExecutor hearBeatExecutor(WebSocketMessageSender webSocketMessageSender) {
        return new HearBeatExecutor(webSocketMessageSender);
    }

}

