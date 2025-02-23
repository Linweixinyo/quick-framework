package org.weixin.framework.mail.core;

import cn.hutool.extra.mail.MailAccount;
import org.weixin.framework.mail.config.MailAccountProperties;

/**
 * 用于动态创建邮件发送账号
 */
public abstract class MailAccountManager {


    /**
     * 构建邮件发送账号
     *
     * @return 邮件发送账号
     */
    public MailAccount buildAccount() {
        return buildAccount(getMailAccountProperties());
    }

    /**
     * 获取邮件发送配置
     *
     * @return 邮件发送
     */
    protected abstract MailAccountProperties getMailAccountProperties();


    private MailAccount buildAccount(MailAccountProperties mailAccountProperties) {
        MailAccount account = new MailAccount();
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
