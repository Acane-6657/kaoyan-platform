package com.kaoyan.security;

/**
 * 权限判定扩展点。
 * <p>
 * 阶段二提供基于角色的默认实现；阶段三接入 sys_role_menu / sys_menu 后，
 * 由数据库权限标识判定（可用 Redis 缓存用户权限集合）。
 */
public interface PermissionChecker {

    boolean hasPermission(LoginUser user, String permission);
}
