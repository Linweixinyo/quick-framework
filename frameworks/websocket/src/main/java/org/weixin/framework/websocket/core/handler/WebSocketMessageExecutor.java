package org.weixin.framework.websocket.core.handler;

import org.springframework.web.socket.WebSocketSession;
import org.weixin.framework.websocket.core.message.WebSocketMessage;

public interface WebSocketMessageExecutor<T extends WebSocketMessage> {

    void execute(WebSocketSession session, T message);

    String getType();

}
