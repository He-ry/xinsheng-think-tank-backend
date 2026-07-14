package com.learn.domain.pojo;

import com.learn.domain.pojo.LoginUser;

import lombok.Data;
/*
 * Exception performing whole class analysis ignored.
 */
public final class UserContext {
    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser getLoginUser() {
        return (LoginUser)CONTEXT.get();
    }

    public static Integer getUserId() {
        LoginUser user = UserContext.getLoginUser();
        return user == null ? null : user.getUserId();
    }

    public static String getUsername() {
        LoginUser user = UserContext.getLoginUser();
        return user == null ? "" : user.getUsername();
    }

    public static String getUserIdStr() {
        LoginUser user = UserContext.getLoginUser();
        return user == null ? "" : user.getUserId().toString();
    }

    public static boolean isLogin() {
        return UserContext.getLoginUser() != null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
