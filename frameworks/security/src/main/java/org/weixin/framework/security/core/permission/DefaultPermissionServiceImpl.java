package org.weixin.framework.security.core.permission;

import java.util.Collections;
import java.util.List;

public class DefaultPermissionServiceImpl extends PermissionService {

    @Override
    protected List<String> getPermissionList(String userId) {
        return Collections.emptyList();
    }

    @Override
    protected List<String> getRoleList(String userId) {
        return Collections.emptyList();
    }
}
