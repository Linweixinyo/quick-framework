package org.weixin.framework.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitMQAutoConfiguration {


    @Bean
    public MessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
