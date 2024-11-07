package org.weixin.framework.security.core.token;

import org.weixin.framework.security.core.info.LoginUserInfo;

public interface TokenVerifyService {

    boolean verifyToken(String token);

    LoginUserInfo getLoginUserInfo(String token);

}
