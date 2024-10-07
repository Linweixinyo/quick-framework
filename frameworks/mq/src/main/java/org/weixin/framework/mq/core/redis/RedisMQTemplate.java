package org.weixin.framework.mq.core.redis;


import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.weixin.framework.mq.core.redis.message.AbstractRedisStreamMessage;

@RequiredArgsConstructor
public class RedisMQTemplate {

    private final StringRedisTemplate stringRedisTemplate;


    public <T extends AbstractRedisStreamMessage> RecordId send(T message) {
        // 发送消息
        return stringRedisTemplate.opsForStream().add(StreamRecords.newRecord()
                .ofObject(JSONUtil.toJsonStr(message))
                .withStreamKey(message.getStreamKey()));
    }




}
