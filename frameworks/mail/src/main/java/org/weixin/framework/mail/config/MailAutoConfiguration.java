package org.weixin.framework.mail.config;


import cn.hutool.extra.mail.MailAccount;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = MailProperties.MAIL_PREFIX, name = "enabled", havingValue = "true")
public class MailAutoConfiguration {


    /**
     * 默认全局邮件发送账号
     *
     * @param mailProperties 邮箱配置
     * @return 邮件发送账号
     */
    @Bean
    public MailAccount mailAccount(MailProperties mailProperties) {
        MailAccount account = new MailAccount();
        MailAccountProperties mailAccountProperties = mailProperties.getMailAccount();
        account.setHost(mailAccountProperties.getHost());
        account.setPort(mailAccountProperties.getPort());
        account.setAuth(mailAccountProperties.getAuth());
        account.setFrom(mailAccountProperties.getFrom());
        account.setUser(mailAccountProperties.getUser());
        account.setPass(mailAccountProperties.getPass());
        account.setSocketFactoryPort(mailAccountProperties.getPort());
        account.setStarttlsEnable(mailAccountProperties.getStarttlsEnable());
        account.setSslEnable(mailAccountProperties.getSslEnable());
        account.setTimeout(mailAccountProperties.getTimeout());
        account.setConnectionTimeout(mailAccountProperties.getConnectionTimeout());
        return account;
    }

}
