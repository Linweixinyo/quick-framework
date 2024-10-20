package org.weixin.framework.websocket.core.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现
 */
public class WebSocketSessionManagerImpl implements WebSocketSessionManager {

    /**
     * sessionId 与 Session 的映射
     */
    private static final Map<String, WebSocketSession> DEFAULT_SESSION_MAP = new ConcurrentHashMap<>();


    @Override
    public void addSession(String id, WebSocketSession session) {
        DEFAULT_SESSION_MAP.put(id, session);
    }

    @Override
    public void removeSession(String id) {
        DEFAULT_SESSION_MAP.remove(id);
    }

    @Override
    public WebSocketSession getSession(String id) {
        return DEFAULT_SESSION_MAP.get(id);
    }
}
