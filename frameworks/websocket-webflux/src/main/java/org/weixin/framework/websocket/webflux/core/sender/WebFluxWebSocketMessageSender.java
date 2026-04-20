package org.weixin.framework.websocket.webflux.core.sender;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.weixin.framework.websocket.webflux.core.message.WebSocketMessage;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionHolder;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionManager;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class WebFluxWebSocketMessageSender {

    private final WebFluxWebSocketSessionManager sessionManager;

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
        WebFluxWebSocketSessionHolder sessionHolder = sessionManager.getSession(id);
        sendTextMessage(sessionHolder, messageText, id);
    }

    public void sendTextMessage(Collection<WebSocketSession> sessions, String messageText) {
        if (sessions == null || sessions.isEmpty()) {
            log.error("[sendTextMessage][sessions 为空]");
            return;
        }
        sessions.forEach(session -> {
            if (session == null) {
                log.error("[sendTextMessage][session 为 null]");
                return;
            }
            WebFluxWebSocketSessionHolder sessionHolder = sessionManager.getSession(session.getId());
            sendTextMessage(sessionHolder, messageText, session.getId());
        });
    }

    private void sendTextMessage(WebFluxWebSocketSessionHolder sessionHolder, String messageText, String sessionId) {
        if (sessionHolder == null) {
            log.error("[sendTextMessage][sessionId({}) 对应会话不存在]", sessionId);
            return;
        }
        if (!sessionHolder.getSession().isOpen()) {
            log.error("[sendTextMessage][sessionId({}) 对应会话已关闭]", sessionId);
            return;
        }
        boolean emitSuccess = sessionHolder.send(messageText);
        if (!emitSuccess) {
            log.error("[sendTextMessage][sessionId({}) 发送失败，消息可能被丢弃]", sessionId);
        }
    }

    private <T extends WebSocketMessage> String buildTextMessage(String type, T message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("type", type);
        jsonObject.putOnce("body", message);
        return jsonObject.toString();
    }

}
