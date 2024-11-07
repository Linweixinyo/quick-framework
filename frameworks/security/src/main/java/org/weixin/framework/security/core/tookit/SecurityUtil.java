package org.weixin.framework.security.core.tookit;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.weixin.framework.security.core.info.LoginUserInfo;

import java.util.Objects;

public final class SecurityUtil {


    public static String getToken(HttpServletRequest request, String header, String parameter, String tokenPrefix) {
        String token = request.getHeader(header);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(parameter);
        }
        if (StrUtil.isBlank(token)) {
            return StrUtil.EMPTY;
        }
        if (token.startsWith(tokenPrefix + StrUtil.SPACE)) {
            return token.substring((tokenPrefix + StrUtil.SPACE).length());
        }
        return token;
    }

    public static String getLoginUserId() {
        LoginUserInfo loginUserInfo = getLoginUser();
        if (Objects.nonNull(loginUserInfo)) {
            return loginUserInfo.getUserId();
        }
        return null;
    }

    public static LoginUserInfo getLoginUser() {
        Authentication authentication = getAuthentication();
        if (Objects.nonNull(authentication) && authentication.getPrincipal() instanceof LoginUserInfo loginUserInfo) {
            return loginUserInfo;
        }
        return null;
    }

    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (Objects.nonNull(context)) {
            return context.getAuthentication();
        }
        return null;
    }

}
