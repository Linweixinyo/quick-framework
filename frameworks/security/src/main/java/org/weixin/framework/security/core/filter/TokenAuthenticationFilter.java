package org.weixin.framework.security.core.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.weixin.framework.security.config.SecurityProperties;
import org.weixin.framework.security.core.info.LoginUserInfo;
import org.weixin.framework.security.core.token.TokenVerifyService;
import org.weixin.framework.security.core.tookit.SecurityUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final TokenVerifyService tokenVerifyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = SecurityUtil.getToken(request, securityProperties.getHeader(), securityProperties.getParameter(), securityProperties.getTokenPrefix());
        if (StrUtil.isNotBlank(token)) {
            LoginUserInfo loginUserInfo = tokenVerifyService.getLoginUserInfo(token);
            if (Objects.nonNull(loginUserInfo)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUserInfo, null, Collections.emptyList());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
