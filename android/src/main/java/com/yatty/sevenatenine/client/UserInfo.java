package com.yatty.sevenatenine.client;

public class UserInfo {
    private static String userName;
    private static String authToken;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserInfo.userName = userName;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        UserInfo.authToken = authToken;
    }
}
