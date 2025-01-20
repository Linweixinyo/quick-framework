package org.weixin.framework.mq.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.weixin.framework.mq.core.redis.RedisMQTemplate;
import org.weixin.framework.mq.core.redis.stream.AbstractRedisStreamMessageListener;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ConditionalOnBean(RedisAutoConfiguration.class)
public class RedisMQAutoConfiguration {

    @Bean
    public RedisMQTemplate redisMQTemplate(StringRedisTemplate stringRedisTemplate) {
        return new RedisMQTemplate(stringRedisTemplate);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(AbstractRedisStreamMessageListener.class)
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(RedisMQTemplate redisMQTemplate, List<AbstractRedisStreamMessageListener> listeners) {
        StringRedisTemplate stringRedisTemplate = redisMQTemplate.getStringRedisTemplate();
        // Stream配置
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .batchSize(10)
                        .targetType(String.class)
                        .build();
        // 创建Stream
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(stringRedisTemplate.getRequiredConnectionFactory(), containerOptions);

        if (CollectionUtil.isNotEmpty(listeners)) {
            String consumerName = buildConsumerName();
            for (AbstractRedisStreamMessageListener listener : listeners) {
                Boolean hasKey = stringRedisTemplate.hasKey(listener.getStreamKey());
                if (Objects.isNull(hasKey) || !hasKey) {
                    // 创建 listener 对应的消费者分组
                    stringRedisTemplate.opsForStream().createGroup(listener.getStreamKey(), listener.getGroup());
                }
                // 创建消费者并且注册
                StreamOffset<String> streamOffset = StreamOffset.create(listener.getStreamKey(), ReadOffset.lastConsumed());

                Consumer consumer = Consumer.from(listener.getGroup(), Optional.ofNullable(listener.getConsumerName())
                        .filter(StrUtil::isNotBlank)
                        .orElse(consumerName));
                StreamMessageListenerContainer.ConsumerStreamReadRequest<String> consumerStreamReadRequest =
                        StreamMessageListenerContainer.StreamReadRequest.builder(streamOffset)
                                .consumer(consumer)
                                // 每个消费者需要手动ack
                                .autoAcknowledge(false)
                                .build();
                container.register(consumerStreamReadRequest, listener);
                // 将pending中的消息进行重新分配
                PendingMessages pendingMessages = stringRedisTemplate.opsForStream().pending(listener.getStreamKey(), consumer, Range.closed("-", "+"), -1);
                if (!pendingMessages.isEmpty()) {
                    for (PendingMessage pendingMessage : pendingMessages) {
                        /*stringRedisTemplate.opsForStream().claim(listener.getStreamKey(), listener.getGroup(), consumer.getName(),
                                Duration.ofSeconds(10), pendingMessage.getId());*/
                        String messageId = pendingMessage.getId().getValue();
                        List<ObjectRecord<String, String>> objectRecords = stringRedisTemplate.opsForStream().range(String.class, listener.getStreamKey(),
                                Range.closed(messageId, messageId));
                        if (CollectionUtil.isNotEmpty(objectRecords)) {
                            listener.onMessage(CollectionUtil.getFirst(objectRecords));
                        }
                    }
                }
            }
        }
        return container;
    }

    /**
     * 构建消费者名字，使用本地 IP + 进程编号的方式。
     * 参考自 RocketMQ clientId 的实现
     *
     * @return 消费者名字
     */
    private static String buildConsumerName() {
        return String.format("%s@%d", SystemUtil.getHostInfo().getAddress(), SystemUtil.getCurrentPID());
    }


}
