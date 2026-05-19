package org.weixin.framework.websocket.webflux.core.session;

public interface WebFluxWebSocketSessionManager {

    void addSession(String id, WebFluxWebSocketSessionHolder sessionHolder);

    void removeSession(String id);

    WebFluxWebSocketSessionHolder getSession(String id);

}
