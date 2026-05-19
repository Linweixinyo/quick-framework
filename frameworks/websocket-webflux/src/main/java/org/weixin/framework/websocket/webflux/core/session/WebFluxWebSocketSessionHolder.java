package org.weixin.framework.websocket.webflux.core.session;

import lombok.Getter;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Getter
public class WebFluxWebSocketSessionHolder {

    private final WebSocketSession session;

    private final Sinks.Many<String> outboundSink;

    public WebFluxWebSocketSessionHolder(WebSocketSession session) {
        this.session = session;
        this.outboundSink = Sinks.many().unicast().onBackpressureBuffer();
    }

    /**
     * 发送文本消息到当前会话
     */
    public boolean send(String messageText) {
        return outboundSink.tryEmitNext(messageText) == Sinks.EmitResult.OK;
    }

    /**
     * 出站消息流，由 WebFlux 统一驱动发送
     */
    public Flux<org.springframework.web.reactive.socket.WebSocketMessage> outboundMessages() {
        return outboundSink.asFlux().map(session::textMessage);
    }

    public void complete() {
        outboundSink.tryEmitComplete();
    }

}
