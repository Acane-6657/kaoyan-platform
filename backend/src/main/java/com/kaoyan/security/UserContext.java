package com.kaoyan.security;

import com.kaoyan.common.exception.BizException;
import com.kaoyan.common.result.ResultCode;

/**
 * 当前登录用户上下文（基于 ThreadLocal，由 JwtInterceptor 写入、请求结束时清理）
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static LoginUser require() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw BizException.of(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    public static Long userId() {
        return require().getUserId();
    }
}
