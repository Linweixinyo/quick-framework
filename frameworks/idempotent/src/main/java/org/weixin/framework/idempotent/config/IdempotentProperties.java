package org.weixin.framework.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = IdempotentProperties.IDEMPOTENT_PREFIX)
public class IdempotentProperties {

    public static final String IDEMPOTENT_PREFIX = "framework.idempotent.config";

    /**
     * Token 幂等 Key 前缀
     */
    private String prefix = "Authorization";

}
