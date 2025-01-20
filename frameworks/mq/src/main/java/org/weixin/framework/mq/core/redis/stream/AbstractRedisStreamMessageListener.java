package org.weixin.framework.mq.core.redis.stream;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.weixin.framework.mq.core.redis.message.AbstractRedisStreamMessage;

import java.lang.reflect.Type;

public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage> implements StreamListener<String, ObjectRecord<String, String>> {

    private final String streamKey;

    private final String group;

    protected final String consumerName;


    protected AbstractRedisStreamMessageListener(String streamKey, String group, String consumerName) {
        this.streamKey = streamKey;
        this.group = group;
        this.consumerName = consumerName;
    }


    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String value = message.getValue();
        T messageObj = JSONUtil.toBean(value, getMessageType());
        try {
            consumeMessageBefore(messageObj);
            onMessage(message.getId(), messageObj);
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

    public String getStreamKey() {
        return streamKey;
    }

    public String getGroup() {
        return group;
    }

    public String getConsumerName() {
        return consumerName;
    }

    protected abstract void onMessage(RecordId recordId, T message);

    protected abstract void consumeMessageBefore(T message);

    protected abstract void consumeMessageAfter(T message);

}
