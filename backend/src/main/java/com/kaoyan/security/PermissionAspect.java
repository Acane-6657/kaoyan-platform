package com.kaoyan.security;

import com.kaoyan.common.exception.BizException;
import com.kaoyan.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionChecker permissionChecker;

    @Before("@annotation(requiresPermission)")
    public void check(RequiresPermission requiresPermission) {
        LoginUser user = UserContext.require();

        String requiredRole = requiresPermission.role();
        if (StringUtils.hasText(requiredRole) && !user.hasRole(requiredRole)) {
            log.warn("用户 {} 缺少角色 {}", user.getUsername(), requiredRole);
            throw BizException.of(ResultCode.FORBIDDEN);
        }

        String permission = requiresPermission.value();
        if (StringUtils.hasText(permission) && !permissionChecker.hasPermission(user, permission)) {
            log.warn("用户 {} 缺少权限 {}", user.getUsername(), permission);
            throw BizException.of(ResultCode.FORBIDDEN);
        }
    }
}
