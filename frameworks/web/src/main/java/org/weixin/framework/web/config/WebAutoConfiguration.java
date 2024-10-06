package org.weixin.framework.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.weixin.framework.web.core.filter.FilterRegistrationUtil;

public class WebAutoConfiguration {


    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return FilterRegistrationUtil.registerFilter(new CorsFilter(source), Integer.MIN_VALUE);
    }


}
