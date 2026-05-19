package org.weixin.framework.websocket.webflux.core.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现
 */
public class WebFluxWebSocketSessionManagerImpl implements WebFluxWebSocketSessionManager {

    /**
     * sessionId 与 SessionHolder 的映射
     */
    private static final Map<String, WebFluxWebSocketSessionHolder> DEFAULT_SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void addSession(String id, WebFluxWebSocketSessionHolder sessionHolder) {
        DEFAULT_SESSION_MAP.put(id, sessionHolder);
    }

    @Override
    public void removeSession(String id) {
        DEFAULT_SESSION_MAP.remove(id);
    }

    @Override
    public WebFluxWebSocketSessionHolder getSession(String id) {
        return DEFAULT_SESSION_MAP.get(id);
    }

}
