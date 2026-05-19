package org.weixin.framework.websocket.webflux.core.handler;

import org.springframework.web.reactive.socket.WebSocketSession;
import org.weixin.framework.websocket.webflux.core.message.WebSocketMessage;
import reactor.core.publisher.Mono;

public interface WebFluxWebSocketMessageExecutor<T extends WebSocketMessage> {

    Mono<Void> execute(WebSocketSession session, T message);

    String getType();

}
