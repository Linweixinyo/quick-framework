package org.weixin.framework.encrypt.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = EncryptProperties.ENCRYPT_PREFIX)
public class EncryptProperties {

    public static final String ENCRYPT_PREFIX = "framework.encrypt.config";

    /**
     * 是否启用加密
     */
    private Boolean enabled;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 私钥路径，以classpath:开头
     */
    private String privateKeyPath;


    public String loadPrivateKey() {
        String privateKey = this.getPrivateKey();
        String privateKeyPath = this.getPrivateKeyPath();
        String result = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(privateKey)) {
            result = privateKey;
        } else if (StrUtil.isNotBlank(privateKeyPath)) {
            result = ResourceUtil.readUtf8Str(privateKeyPath);
        }
        return result.replace("-----BEGIN PRIVATE KEY-----", StrUtil.EMPTY)
                .replace("-----END PRIVATE KEY-----", StrUtil.EMPTY)
                .replaceAll("\\s+", StrUtil.EMPTY);
    }

}
