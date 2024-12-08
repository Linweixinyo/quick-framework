package org.weixin.framework.sse.core;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现
 */
@Slf4j
public class SseEmitterManagerImpl implements SseEmitterManager {

    /**
     * SseEmitter 的映射
     */
    private static final Map<String, Map<String, SseEmitter>> DEFAULT_SSE_MAP = new ConcurrentHashMap<>();

    @Override
    public void connect(String id, String sseMarkId, SseEmitter sseEmitter) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(sseMarkId)) {
            return;
        }
        Map<String, SseEmitter> sseMarkIdToSse = DEFAULT_SSE_MAP.computeIfAbsent(id, key -> new ConcurrentHashMap<>());
        if (sseMarkIdToSse.containsKey(sseMarkId)) {
            SseEmitter sseEmitterOfExist = sseMarkIdToSse.get(sseMarkId);
            sseEmitterOfExist.complete();
        }
        sseMarkIdToSse.put(sseMarkId, sseEmitter);
        sseEmitter.onCompletion(() -> sseMarkIdToSse.remove(sseMarkId));
        sseEmitter.onTimeout(() -> sseMarkIdToSse.remove(sseMarkId));
        sseEmitter.onError((ex) -> {
            log.error("sseEmitter onError: {}", ex.getMessage(), ex);
            sseMarkIdToSse.remove(sseMarkId);
        });
        try {
            // 向客户端发送一条连接成功的事件
            sseEmitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            // 如果发送消息失败，则从映射表中移除 emitter
            sseMarkIdToSse.remove(sseMarkId);
        }
    }

    @Override
    public boolean disconnect(String id, String sseMarkId) {
        Map<String, SseEmitter> sseMarkIdToSse = DEFAULT_SSE_MAP.get(id);
        if (Objects.nonNull(sseMarkIdToSse)) {
            try {
                SseEmitter sseEmitter = sseMarkIdToSse.get(sseMarkId);
                if (Objects.nonNull(sseEmitter)) {
                    sseEmitter.send(SseEmitter.event().comment("disconnected"));
                    sseEmitter.complete();
                    return true;
                }
            } catch (Exception ex) {
                log.error("sseEmitter disconnect: {}", ex.getMessage(), ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public void sendMessage(String id, String sseMarkId, Object[] dataArray) {
        Map<String, SseEmitter> sseMarkIdToSse = DEFAULT_SSE_MAP.get(id);
        if (Objects.nonNull(sseMarkIdToSse)) {
            SseEmitter sseEmitter = sseMarkIdToSse.get(sseMarkId);
            if (Objects.isNull(sseEmitter)) {
                log.error("SseEmitter ID: {} sendMessage error：sseEmitter No Exist", id);
                return;
            }
            try {
                SseEmitter.SseEventBuilder message = SseEmitter.event().name("message");
                for (Object data : dataArray) {
                    message.data(data);
                }
                sseEmitter.send(message);
            } catch (Exception ex) {
                log.error("sseEmitter sendMessage: {}", ex.getMessage(), ex);
            }
        }
    }

    @Override
    public SseEmitter getSseEmitter(String id, String sseMarkId) {
        Map<String, SseEmitter> sseMarkIdToSse = DEFAULT_SSE_MAP.get(id);
        if (Objects.nonNull(sseMarkIdToSse)) {
            return sseMarkIdToSse.get(sseMarkId);
        }
        return null;
    }
}
