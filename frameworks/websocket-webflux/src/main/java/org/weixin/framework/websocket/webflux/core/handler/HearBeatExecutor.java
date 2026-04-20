package org.weixin.framework.websocket.webflux.core.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.weixin.framework.websocket.webflux.core.message.HearBeatRequest;
import org.weixin.framework.websocket.webflux.core.message.HearBeatResponse;
import org.weixin.framework.websocket.webflux.core.sender.WebFluxWebSocketMessageSender;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
public class HearBeatExecutor implements WebFluxWebSocketMessageExecutor<HearBeatRequest> {

    private final WebFluxWebSocketMessageSender webSocketMessageSender;

    @Override
    public Mono<Void> execute(WebSocketSession session, HearBeatRequest message) {
        String content = message.getContent();
        if (Objects.equals("PING", content)) {
            webSocketMessageSender.send(session, new HearBeatResponse().setContent("PONG"));
        }
        return Mono.empty();
    }

    @Override
    public String getType() {
        return HearBeatRequest.TYPE;
    }
}
