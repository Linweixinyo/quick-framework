package org.weixin.framework.security.core.permission;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.weixin.framework.security.core.tookit.SecurityUtil;

import java.util.Arrays;
import java.util.List;

public abstract class PermissionService {


    public boolean hasPermissions(String permission) {
        return hasAnyPermissions(permission);
    }


    public boolean hasAnyPermissions(String... permissions) {
        String loginUserId = SecurityUtil.getLoginUserId();
        if (StrUtil.isBlank(loginUserId)) {
            return false;
        }
        List<String> permissionList = getPermissionList(loginUserId);
        if (CollectionUtil.isEmpty(permissionList)) {
            return false;
        }
        return permissionList.stream()
                .anyMatch(permission -> Arrays.asList(permissions).contains(permission));
    }


    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }


    public boolean hasAnyRoles(String... roles) {
        String loginUserId = SecurityUtil.getLoginUserId();
        if (StrUtil.isBlank(loginUserId)) {
            return false;
        }
        List<String> roleList = getRoleList(loginUserId);
        if (CollectionUtil.isEmpty(roleList)) {
            return false;
        }
        return roleList.stream()
                .anyMatch(role -> Arrays.asList(roles).contains(role));
    }


    protected abstract List<String> getPermissionList(String userId);


    protected abstract List<String> getRoleList(String userId);

}
