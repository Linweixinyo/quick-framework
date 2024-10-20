package org.weixin.framework.websocket.core.session;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionManager {

    void addSession(String id, WebSocketSession session);

    void removeSession(String id);

    WebSocketSession getSession(String id);

}
