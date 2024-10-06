package org.weixin.framework.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.common.toolkit.context.ApplicationContextHolder;


public class CommonAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }


}
