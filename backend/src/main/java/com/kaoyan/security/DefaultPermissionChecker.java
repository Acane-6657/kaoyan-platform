package com.kaoyan.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认权限判定：管理员放行全部权限，学生放行非 admin 前缀的权限。
 * <p>
 * TODO 阶段三：改为查询 sys_role_menu + sys_menu 的权限标识，并用 Redis 缓存。
 */
@Slf4j
@Component
public class DefaultPermissionChecker implements PermissionChecker {

    @Override
    public boolean hasPermission(LoginUser user, String permission) {
        if (user.isAdmin()) {
            return true;
        }
        boolean allowed = !permission.startsWith("admin:") && !permission.startsWith("system:");
        if (!allowed) {
            log.debug("学生 {} 被拒绝访问权限 {}", user.getUsername(), permission);
        }
        return allowed;
    }
}
