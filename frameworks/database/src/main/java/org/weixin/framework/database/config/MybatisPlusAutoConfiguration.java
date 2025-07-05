package org.weixin.framework.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.database.core.handler.CustomizeDBFieldHandler;
import org.weixin.framework.database.core.handler.DefaultDBFieldHandler;

@RequiredArgsConstructor
public class MybatisPlusAutoConfiguration {

    private final ObjectProvider<CustomizeDBFieldHandler> customizeDBFieldHandlerObjectProvider;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        CustomizeDBFieldHandler customizeDBFieldHandler = customizeDBFieldHandlerObjectProvider.getIfAvailable(() -> null);
        return new DefaultDBFieldHandler(customizeDBFieldHandler);
    }

}
