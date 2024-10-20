package org.weixin.framework.websocket.core.session;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;


public class WebSocketSessionDecorator extends WebSocketHandlerDecorator {

    /**
     * 发送时间的限制，单位：毫秒
     */
    private static final Integer SEND_TIME_LIMIT = 1000 * 5;
    /**
     * 发送消息缓冲上线，单位：bytes
     */
    private static final Integer BUFFER_SIZE_LIMIT = 1024 * 100;

    private final WebSocketSessionManager sessionManager;

    public WebSocketSessionDecorator(WebSocketHandler delegate, WebSocketSessionManager sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后
        session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        // 存储session
        onOpen(sessionManager, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 移除session
        onClose(sessionManager, session);
    }

    protected void onOpen(WebSocketSessionManager sessionManager, WebSocketSession session) {
        // default
        sessionManager.addSession(session.getId(), session);
    }

    protected void onClose(WebSocketSessionManager sessionManager, WebSocketSession session) {
        // default
        sessionManager.removeSession(session.getId());
    }


}
