package org.weixin.framework.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.weixin.framework.security.core.filter.TokenAuthenticationFilter;
import org.weixin.framework.security.core.handler.AccessDeniedHandlerImpl;
import org.weixin.framework.security.core.handler.AuthenticationEntryPointImpl;
import org.weixin.framework.security.core.permission.DefaultPermissionServiceImpl;
import org.weixin.framework.security.core.permission.PermissionService;
import org.weixin.framework.security.core.token.DefaultTokenVerifyServiceImpl;
import org.weixin.framework.security.core.token.TokenVerifyService;

@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    private final SecurityProperties securityProperties;

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(TokenVerifyService.class)
    public TokenVerifyService tokenVerifyService() {
        return new DefaultTokenVerifyServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PermissionService.class)
    public PermissionService permissionService() {
        return new DefaultPermissionServiceImpl();
    }


    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenVerifyService tokenVerifyService) {
        return new TokenAuthenticationFilter(securityProperties, tokenVerifyService);
    }

    @Bean
    public FilterRegistrationBean<TokenAuthenticationFilter> tokenAuthenticationFilterRegistration(TokenAuthenticationFilter tokenAuthenticationFilter) {
        FilterRegistrationBean<TokenAuthenticationFilter> filterRegistration = new FilterRegistrationBean<>(tokenAuthenticationFilter);
        filterRegistration.setEnabled(false);
        return filterRegistration;
    }


}
