package org.weixin.framework.security.config;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.weixin.framework.security.core.filter.TokenAuthenticationFilter;

import java.util.*;


@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration implements ApplicationContextAware {


    private final SecurityProperties securityProperties;

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    private final List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers;

    private ApplicationContext applicationContext;

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint));

        Map<String, Set<String>> permitAllUrls = getPermitAllUrls();

        // 配置请求路径权限
        httpSecurity
                .authorizeHttpRequests(registry -> authorizeRequestsCustomizers.forEach(authorizeRequestsCustomizer -> authorizeRequestsCustomizer.customize(registry)))
                .authorizeHttpRequests(registry -> {
                    permitAllUrls.forEach((methodName, urls) -> {
                        registry.requestMatchers(HttpMethod.valueOf(methodName), urls.toArray(new String[0])).permitAll();
                    });
                    registry.requestMatchers(new RegexRequestMatcher("^.*\\.(html|js|css|ico)$", HttpMethod.GET.name())).permitAll()
                            .requestMatchers(securityProperties.getIgnoreUrls().toArray(new String[0])).permitAll()
                            .anyRequest().authenticated();
                });
        httpSecurity.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }


    public Map<String, Set<String>> getPermitAllUrls() {
        Map<String, Set<String>> methodToUrls = new HashMap<>();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach(((requestMappingInfo, handlerMethod) -> {
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
                return;
            }
            Set<String> urls = new HashSet<>();
            if (CollectionUtil.isNotEmpty(requestMappingInfo.getPatternValues())) {
                urls.addAll(requestMappingInfo.getPatternValues());
            }
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = methodsCondition.getMethods();
            if (CollectionUtil.isNotEmpty(methods)) {
                for (RequestMethod method : methods) {
                    Set<String> urlSet = methodToUrls.getOrDefault(method.name(), new HashSet<>());
                    urlSet.addAll(urls);
                    methodToUrls.put(method.name(), urlSet);
                }
            }
        }));
        return methodToUrls;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
