package org.weixin.framework.encrypt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.weixin.framework.encrypt.core.callback.EncryptProcessorCallback;
import org.weixin.framework.encrypt.core.converter.EncryptHttpMessageConverter;
import org.weixin.framework.encrypt.core.resolver.EncryptableArgumentResolver;
import org.weixin.framework.encrypt.core.service.SignatureService;

@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnProperty(prefix = EncryptProperties.ENCRYPT_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class EncryptAutoConfiguration {

    private final EncryptProperties encryptProperties;

    private final ObjectProvider<EncryptProcessorCallback> encryptHandlerObjectProvider;

    @Bean
    public SignatureService signatureService(StringRedisTemplate stringRedisTemplate) {
        return new SignatureService(stringRedisTemplate);
    }

    @Bean
    @Order(value = 0)
    public EncryptHttpMessageConverter encryptHttpMessageConverter(SignatureService signatureService) {
        EncryptProcessorCallback encryptProcessorCallback = encryptHandlerObjectProvider.getIfAvailable(() -> new EncryptProcessorCallback() {
        });
        return new EncryptHttpMessageConverter(encryptProperties, signatureService, encryptProcessorCallback);
    }

    @Bean
    public EncryptableArgumentResolver encryptableArgumentResolver(SignatureService signatureService) {
        EncryptProcessorCallback encryptProcessorCallback = encryptHandlerObjectProvider.getIfAvailable(() -> new EncryptProcessorCallback() {
        });
        return new EncryptableArgumentResolver(encryptProperties, signatureService, encryptProcessorCallback);
    }

}
