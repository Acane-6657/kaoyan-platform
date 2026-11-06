package com.kaoyan.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口级权限校验注解，配合 PermissionAspect 使用。
 * <p>
 * value：权限标识，如 course:add；role：要求的角色编码，如 ADMIN。
 * 两者可同时指定，需全部满足。
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /** 权限标识，如 course:add */
    String value() default "";

    /** 要求的角色编码，如 ADMIN；为空表示不限角色 */
    String role() default "";
}
