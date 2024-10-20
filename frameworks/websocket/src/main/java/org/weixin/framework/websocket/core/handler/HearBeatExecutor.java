package org.weixin.framework.websocket.core.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;
import org.weixin.framework.websocket.core.message.HearBeatRequest;
import org.weixin.framework.websocket.core.message.HearBeatResponse;
import org.weixin.framework.websocket.core.sender.WebSocketMessageSender;

import java.util.Objects;

@RequiredArgsConstructor
public class HearBeatExecutor implements WebSocketMessageExecutor<HearBeatRequest> {

    private final WebSocketMessageSender webSocketMessageSender;

    @Override
    public void execute(WebSocketSession session, HearBeatRequest message) {
        String content = message.getContent();
        if (Objects.equals("PING", content)) {
            webSocketMessageSender.send(session, new HearBeatResponse().setContent("PONG"));
        }
    }

    @Override
    public String getType() {
        return HearBeatRequest.TYPE;
    }
}
