package org.weixin.framework.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;


@Data
@ConfigurationProperties(prefix = MailProperties.MAIL_PREFIX)
public class MailProperties {

    public static final String MAIL_PREFIX = "framework.mail.config";

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 邮件发送账号
     */
    @NestedConfigurationProperty
    private MailAccountProperties mailAccount;

}