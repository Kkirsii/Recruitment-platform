package com.kkirsii.recruitmentplatform.util;

public class UserContext {
    private static final ThreadLocal<String> userEmail = new ThreadLocal<>();

    public static void setEmail(String email) {
        userEmail.set(email);
    }

    public static String getEmail() {
        return userEmail.get();
    }

    public static void clear() {
        userEmail.remove();
    }
}
