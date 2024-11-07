package org.weixin.framework.security.core.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.weixin.framework.web.core.exception.BaseErrorCode;
import org.weixin.framework.web.core.res.Results;
import org.weixin.framework.web.toolkit.ServletUtil;

@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        log.error("[Security Exception][Access URL({}) 时，Not Login]", request.getRequestURI(), authException);
        ServletUtil.writeJSON(response, Results.failure(BaseErrorCode.UNAUTHORIZED_ERROR));
    }
}
