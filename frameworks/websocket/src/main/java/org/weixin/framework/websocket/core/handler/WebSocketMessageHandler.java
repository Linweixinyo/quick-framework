package org.weixin.framework.websocket.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.weixin.framework.websocket.core.message.WebSocketMessage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class WebSocketMessageHandler extends TextWebSocketHandler {

    /**
     * 消息处理器
     */
    private final Map<String, WebSocketMessageExecutor> executorMap = new HashMap<>();

    public WebSocketMessageHandler(List<? extends WebSocketMessageExecutor> executors) {
        for (WebSocketMessageExecutor executor : executors) {
            executorMap.put(executor.getType(), executor);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 空消息
        if (message.getPayloadLength() == 0) {
            return;
        }
        String payload = message.getPayload();
        if (!JSONUtil.isTypeJSON(payload)) {
            log.error("[onMessage][message{} is not JSON String]", message);
            return;
        }
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String messageType = jsonObject.getStr("type");
        if (StrUtil.isBlank(messageType)) {
            log.error("[onMessage][messageType is null or empty]");
            return;
        }
        WebSocketMessageExecutor messageExecutor = executorMap.get(messageType);
        if (Objects.isNull(messageExecutor)) {
            log.error("[onMessage][messageType({}) no exist messageExecutor]", messageType);
            return;
        }
        Class<? extends WebSocketMessage> messageClass = getMessageClass(messageExecutor);
        String body = jsonObject.getStr("body");
        if (StrUtil.isBlank(body)) {
            log.error("[onMessage][body is null or empty]");
            return;
        }
        WebSocketMessage webSocketMessage = JSONUtil.toBean(body, messageClass);
        messageExecutor.execute(session, webSocketMessage);
    }

    public Class<? extends WebSocketMessage> getMessageClass(WebSocketMessageExecutor messageExecutor) {
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(messageExecutor);
        // 获得接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while (0 == interfaces.length && Objects.nonNull(superclass)) { // 此处，是以父类的接口为准
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        // 遍历 interfaces 数组
        for (Type type : interfaces) {
            // 要求 type 是泛型参数
            if (type instanceof ParameterizedType parameterizedType) {
                // 要求是 MessageHandler 接口
                if (Objects.equals(parameterizedType.getRawType(), WebSocketMessageExecutor.class)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    // 取首个元素
                    if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                        return (Class<WebSocketMessage>) actualTypeArguments[0];
                    } else {
                        throw new IllegalStateException(String.format("messageExecutor(%s) unable get messageType", messageExecutor));
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("messageExecutor(%s) unable get messageType", messageExecutor));
    }
}
