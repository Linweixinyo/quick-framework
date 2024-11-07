package org.weixin.framework.security.core.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.weixin.framework.security.core.tookit.SecurityUtil;
import org.weixin.framework.web.core.exception.BaseErrorCode;
import org.weixin.framework.web.core.res.Results;
import org.weixin.framework.web.toolkit.ServletUtil;

@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        log.error("[Security Exception][Access URL({}) 时，User({}) Forbidden]", request.getRequestURI(),
                SecurityUtil.getLoginUserId(), accessDeniedException);
        ServletUtil.writeJSON(response, Results.failure(BaseErrorCode.FORBIDDEN_ERROR));
    }
}
