package org.weixin.framework.designpattern.demo.state;

import org.springframework.aop.framework.AopProxyUtils;
import org.weixin.framework.designpattern.state.BaseEvent;
import org.weixin.framework.designpattern.state.BaseState;
import org.weixin.framework.designpattern.state.EventHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StateTest {

    private static final Map<EventHandler<? extends BaseState, ? extends BaseEvent>, BaseState> HANDLER = new HashMap<>();

    public static void main(String[] args) {
        OrderAToBHandler orderAToBHandler = new OrderAToBHandler();
        OrderBToCHandler orderBToCHandler = new OrderBToCHandler();
        HANDLER.put(orderAToBHandler, orderAToBHandler.getTargetState());
        HANDLER.put(orderBToCHandler, orderBToCHandler.getTargetState());

        // B To C
        OrderBState orderBState = new OrderBState();
        OrderBToCEvent orderBToCEvent = new OrderBToCEvent();
        getHandlerEvent(orderBState, orderBToCEvent);
        // A To B
        OrderAState orderAState = new OrderAState();
        OrderAToBEvent orderAToBEvent = new OrderAToBEvent();
        EventHandler handlerEvent = getHandlerEvent(orderAState, orderAToBEvent);
        handlerEvent.execute(orderAState, orderAToBEvent);
        // 只需要考虑如何获取State和Event
    }

    public static EventHandler getHandlerEvent(BaseState state, BaseEvent event) {
        if(Objects.isNull(state) || Objects.isNull(event)) {
            throw new IllegalStateException("State or event is null");
        }
        for (EventHandler<? extends BaseState, ? extends BaseEvent> eventHandler : HANDLER.keySet()) {
            Class<? extends BaseState> stateClazz = getEventClass(eventHandler, BaseState.class);
            Class<? extends BaseEvent> eventClazz = getEventClass(eventHandler, BaseEvent.class);
            if(state.getClass().isAssignableFrom(stateClazz) && event.getClass().isAssignableFrom(eventClazz)) {
                return eventHandler;
            }
        }
        throw new IllegalStateException(String.format("State[%s] Event[%s] Not found eventHandler", state, event));
    }

    public static  <T> Class<? extends T> getEventClass(EventHandler<? extends BaseState, ? extends BaseEvent> eventHandler, Class<T> clazz) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(eventHandler);
        // 获得接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) { // 此处，是以父类的接口为准
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            // 遍历 interfaces 数组
            for (Type type : interfaces) {
                // 要求 type 是泛型参数
                if (type instanceof ParameterizedType parameterizedType) {
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(), EventHandler.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            for (Type actualTypeArgument : actualTypeArguments) {
                                if(actualTypeArgument instanceof Class<?> actualClazz && clazz.isAssignableFrom(actualClazz)) {
                                    return (Class<? extends T>) actualClazz;
                                }
                            }
                        } else {
                            throw new IllegalStateException(String.format("EventHandler(%s) Unable to get type", eventHandler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("EventHandler(%s) Unable to get type", eventHandler));
    }
}
