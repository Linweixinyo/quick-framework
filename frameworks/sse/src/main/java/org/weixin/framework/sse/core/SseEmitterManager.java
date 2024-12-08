package org.weixin.framework.sse.core;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterManager {

    void connect(String id, String sseMarkId, SseEmitter sseEmitter);

    void sendMessage(String id, String sseMarkId, Object[] dataArray);

    boolean disconnect(String id, String sseMarkId);

    SseEmitter getSseEmitter(String id, String sseMarkId);

}
