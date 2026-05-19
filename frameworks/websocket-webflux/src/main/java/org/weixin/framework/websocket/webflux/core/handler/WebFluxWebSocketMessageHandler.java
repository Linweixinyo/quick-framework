package org.weixin.framework.websocket.webflux.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.weixin.framework.websocket.webflux.core.message.WebSocketMessage;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionHolder;
import org.weixin.framework.websocket.webflux.core.session.WebFluxWebSocketSessionManager;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class WebFluxWebSocketMessageHandler implements WebSocketHandler {

    /**
     * 消息处理器
     */
    private final Map<String, WebFluxWebSocketMessageExecutor<?>> executorMap = new HashMap<>();

    private final WebFluxWebSocketSessionManager sessionManager;

    public WebFluxWebSocketMessageHandler(WebFluxWebSocketSessionManager sessionManager,
                                          List<? extends WebFluxWebSocketMessageExecutor<?>> executors) {
        this.sessionManager = sessionManager;
        for (WebFluxWebSocketMessageExecutor<?> executor : executors) {
            executorMap.put(executor.getType(), executor);
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        WebFluxWebSocketSessionHolder sessionHolder = new WebFluxWebSocketSessionHolder(session);
        onOpen(sessionManager, sessionHolder);

        Mono<Void> inbound = session.receive()
                .map(org.springframework.web.reactive.socket.WebSocketMessage::getPayloadAsText)
                .concatMap(payload -> handleTextMessage(session, payload)
                        .onErrorResume(ex -> {
                            log.error("[handle][处理消息异常 payload={}]", payload, ex);
                            return Mono.empty();
                        }))
                .onErrorResume(ex -> {
                    log.error("[handle][接收消息异常 sessionId={}]", session.getId(), ex);
                    return Mono.empty();
                })
                .then();

        Mono<Void> outbound = session.send(sessionHolder.outboundMessages())
                .onErrorResume(ex -> {
                    log.error("[handle][发送消息异常 sessionId={}]", session.getId(), ex);
                    return Mono.empty();
                });

        return Mono.firstWithSignal(inbound, outbound)
                .doFinally(signalType -> onClose(sessionManager, sessionHolder));
    }

    private Mono<Void> handleTextMessage(WebSocketSession session, String payload) {
        // 空消息
        if (StrUtil.isBlank(payload)) {
            return Mono.empty();
        }
        if (!JSONUtil.isTypeJSON(payload)) {
            log.error("[handleTextMessage][message={} 非 JSON 字符串]", payload);
            return Mono.empty();
        }
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String messageType = jsonObject.getStr("type");
        if (StrUtil.isBlank(messageType)) {
            log.error("[handleTextMessage][messageType 为空]");
            return Mono.empty();
        }
        WebFluxWebSocketMessageExecutor<?> messageExecutor = executorMap.get(messageType);
        if (Objects.isNull(messageExecutor)) {
            log.error("[handleTextMessage][messageType({}) 未找到执行器]", messageType);
            return Mono.empty();
        }
        String body = jsonObject.getStr("body");
        if (StrUtil.isBlank(body)) {
            log.error("[handleTextMessage][body 为空]");
            return Mono.empty();
        }
        Class<? extends WebSocketMessage> messageClass = getMessageClass(messageExecutor);
        WebSocketMessage webSocketMessage = JSONUtil.toBean(body, messageClass);
        return executeMessage(messageExecutor, session, webSocketMessage);
    }

    protected void onOpen(WebFluxWebSocketSessionManager sessionManager, WebFluxWebSocketSessionHolder sessionHolder) {
        sessionManager.addSession(sessionHolder.getSession().getId(), sessionHolder);
    }

    protected void onClose(WebFluxWebSocketSessionManager sessionManager, WebFluxWebSocketSessionHolder sessionHolder) {
        sessionManager.removeSession(sessionHolder.getSession().getId());
        sessionHolder.complete();
    }

    @SuppressWarnings("unchecked")
    private <T extends WebSocketMessage> Mono<Void> executeMessage(WebFluxWebSocketMessageExecutor<?> messageExecutor,
                                                                   WebSocketSession session,
                                                                   T message) {
        return ((WebFluxWebSocketMessageExecutor<T>) messageExecutor).execute(session, message);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends WebSocketMessage> getMessageClass(WebFluxWebSocketMessageExecutor<?> messageExecutor) {
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(messageExecutor);
        // 获得接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while (interfaces.length == 0 && Objects.nonNull(superclass)) { // 此处，是以父类的接口为准
            interfaces = superclass.getGenericInterfaces();
            superclass = superclass.getSuperclass();
        }
        // 遍历 interfaces 数组
        for (Type type : interfaces) {
            // 要求 type 是泛型参数
            if (type instanceof ParameterizedType parameterizedType) {
                // 要求是 MessageHandler 接口
                if (Objects.equals(parameterizedType.getRawType(), WebFluxWebSocketMessageExecutor.class)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    // 取首个元素
                    if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                        return (Class<WebSocketMessage>) actualTypeArguments[0];
                    }
                    throw new IllegalStateException(String.format("messageExecutor(%s) unable get messageType", messageExecutor));
                }
            }
        }
        throw new IllegalStateException(String.format("messageExecutor(%s) unable get messageType", messageExecutor));
    }

}
