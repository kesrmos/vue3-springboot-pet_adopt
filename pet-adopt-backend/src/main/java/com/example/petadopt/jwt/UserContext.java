package com.example.petadopt.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录人信息（ThreadLocal 保存，随请求生命周期存在）
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    /** 当前登录用户 id，未登录时抛异常（拦截器已保证登录，正常不会发生） */
    public static Long getUserId() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new IllegalStateException("当前请求未获取到登录用户信息");
        }
        return user.getUserId();
    }

    public static Integer getRole() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getRole();
    }

    public static void clear() {
        HOLDER.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginUser {
        private Long userId;
        private String username;
        private Integer role;
    }
}
