package org.weixin.framework.mq.core.redis.stream;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.weixin.framework.mq.core.redis.message.AbstractRedisStreamMessage;

import java.lang.reflect.Type;

public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage> implements StreamListener<String, ObjectRecord<String, String>> {


    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String value = message.getValue();
        T messageObj = JSONUtil.toBean(value, getMessageType());
        try {
            consumeMessageBefore(messageObj);
            onMessage(messageObj);
        } finally {
            consumeMessageAfter(messageObj);
        }
    }

    private Class<T> getMessageType() {
        Type typeArgument = TypeUtil.getTypeArgument(this.getClass(), 0);
        if (typeArgument == null) {
            throw new IllegalStateException(String.format("[%s] Undefine MessageType", getClass().getName()));
        }
        return (Class<T>) typeArgument;
    }


    protected abstract void onMessage(T message);

    protected abstract void consumeMessageBefore(T message);

    protected abstract void consumeMessageAfter(T message);

}
