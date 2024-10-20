package org.weixin.framework.websocket.core.sender;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.weixin.framework.websocket.core.message.WebSocketMessage;
import org.weixin.framework.websocket.core.session.WebSocketSessionManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageSender {

    private final WebSocketSessionManager sessionManager;

    public <T extends WebSocketMessage> void send(WebSocketSession session, T message) {
        String messageText = buildTextMessage(message.getType(), message);
        sendTextMessage(Collections.singletonList(session), messageText);
    }

    public <T extends WebSocketMessage> void send(Collection<WebSocketSession> sessions, T message) {
        String messageText = buildTextMessage(message.getType(), message);
        sendTextMessage(sessions, messageText);
    }

    public <T extends WebSocketMessage> void send(String id, T message) {
        String messageText = buildTextMessage(message.getType(), message);
        sendTextMessage(Collections.singletonList(sessionManager.getSession(id)), messageText);
    }

    public void sendTextMessage(Collection<WebSocketSession> sessions, String messageText) {
        sessions.forEach(session -> {
            if (session == null) {
                log.error("[sendTextMessage][session 为 null]");
                return;
            }
            if (!session.isOpen()) {
                log.error("[sendTextMessage][session is not Open]");
                return;
            }
            try {
                session.sendMessage(new TextMessage(messageText));
            } catch (IOException e) {
                log.error("[doSend][send error message({})]", messageText, e);
            }
        });
    }

    private <T extends WebSocketMessage> String buildTextMessage(String type, T message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("type", type);
        jsonObject.putOnce("body", message);
        return jsonObject.toString();
    }

}
