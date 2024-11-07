package org.weixin.framework.security.core.info;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginUserInfo {


    private String userId;


}
