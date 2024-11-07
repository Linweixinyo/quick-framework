package org.weixin.framework.security.core.token;

import cn.hutool.core.util.StrUtil;
import org.weixin.framework.security.core.info.LoginUserInfo;

public class DefaultTokenVerifyServiceImpl implements TokenVerifyService {

    @Override
    public boolean verifyToken(String token) {
        return StrUtil.isNotBlank(token);
    }

    @Override
    public LoginUserInfo getLoginUserInfo(String token) {
        if (verifyToken(token)) {
            LoginUserInfo loginUserInfo = new LoginUserInfo();
            loginUserInfo.setUserId(token);
            return loginUserInfo;
        }
        return null;
    }
}
