package com.yatty.sevenatenine.client;

import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;

public class SessionInfo {
    private static String userName;
    private static String authToken;
    private static PublicLobbyInfo sPublicLobbyInfo;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        SessionInfo.userName = userName;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        SessionInfo.authToken = authToken;
    }

    public static PublicLobbyInfo getPublicLobbyInfo() {
        return sPublicLobbyInfo;
    }

    public static void setPublicLobbyInfo(PublicLobbyInfo publicLobbyInfo) {
        sPublicLobbyInfo = publicLobbyInfo;
    }
}
