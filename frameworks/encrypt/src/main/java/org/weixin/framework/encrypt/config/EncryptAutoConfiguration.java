package org.weixin.framework.encrypt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.weixin.framework.encrypt.core.converter.EncryptHttpMessageConverter;
import org.weixin.framework.encrypt.core.resolver.EncryptableArgumentResolver;

import java.util.List;

@RequiredArgsConstructor
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnProperty(prefix = EncryptProperties.ENCRYPT_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class EncryptAutoConfiguration implements WebMvcConfigurer {

    private final EncryptProperties encryptProperties;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EncryptableArgumentResolver(encryptProperties));
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.set(i, new EncryptHttpMessageConverter(encryptProperties));
            }
        }
    }
}
