package org.weixin.framework.sse.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.sse.core.SseEmitterManager;
import org.weixin.framework.sse.core.SseEmitterManagerImpl;


@RequiredArgsConstructor
@EnableConfigurationProperties(SseProperties.class)
@ConditionalOnProperty(prefix = SseProperties.SSE_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class SseAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(SseEmitterManager.class)
    public SseEmitterManager sseEmitterManager() {
        return new SseEmitterManagerImpl();
    }


}
