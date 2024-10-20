package org.weixin.framework.designpattern.state;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventHandlerContext implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private static final Map<EventHandler, BaseState> HANDLER = new HashMap<>();

    public EventHandler getHandlerEvent(BaseState state, BaseEvent event) {
        if(Objects.isNull(state) || Objects.isNull(event)) {
            throw new IllegalStateException("State or event is null");
        }
        for (EventHandler eventHandler : HANDLER.keySet()) {
            Class<BaseState> stateClazz = getEventClass(eventHandler, BaseState.class);
            Class<BaseEvent> eventClazz = getEventClass(eventHandler, BaseEvent.class);
            if(state.getClass().isAssignableFrom(stateClazz) && event.getClass().isAssignableFrom(eventClazz)) {
                return eventHandler;
            }
        }
        throw new IllegalStateException(String.format("State[%s] Event[%s] Not found eventHandler", state.getClass().getSimpleName(), event.getClass().getSimpleName()));
    }

    public <T> Class<T> getEventClass(EventHandler eventHandler, Class<T> clazz) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(eventHandler);
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
                if (Objects.equals(parameterizedType.getRawType(), EventHandler.class)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    // 取首个元素
                    if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                        for (Type actualTypeArgument : actualTypeArguments) {
                            if(actualTypeArgument instanceof Class<?> actualClazz && clazz.isAssignableFrom(actualClazz)) {
                                return (Class<T>) actualClazz;
                            }
                        }
                    } else {
                        throw new IllegalStateException(String.format("EventHandler(%s) Unable to get type", eventHandler.getClass().getSimpleName()));
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("EventHandler(%s) Unable to get type", eventHandler.getClass().getSimpleName()));
    }



    @Override
    public void afterPropertiesSet() {
        applicationContext.getBeansOfType(EventHandler.class).values()
                .forEach(eventHandler -> HANDLER.put(eventHandler, eventHandler.getTargetState()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
