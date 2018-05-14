package com.yatty.sevenatenine.client.auth;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;

public class SessionInfo {
    private static String userName;
    private static int userRating;
    private static String authToken;
    private static String gameId;
    private static PublicLobbyInfo sPublicLobbyInfo;
    private static PrivateLobbyInfo sPrivateLobbyInfo;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        SessionInfo.userName = userName;
    }

    public static int getUserRating() {
        return userRating;
    }

    public static void setUserRating(int userRating) {
        SessionInfo.userRating = userRating;
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

    public static String getGameId() {
        return getPublicLobbyInfo().getLobbyId();
    }

    public static PublicLobbyInfo getsPublicLobbyInfo() {
        return sPublicLobbyInfo;
    }

    public static void setsPublicLobbyInfo(PublicLobbyInfo sPublicLobbyInfo) {
        SessionInfo.sPublicLobbyInfo = sPublicLobbyInfo;
    }

    public static PrivateLobbyInfo getPrivateLobbyInfo() {
        return sPrivateLobbyInfo;
    }

    public static void setPrivateLobbyInfo(PrivateLobbyInfo privateLobbyInfo) {
        sPrivateLobbyInfo = privateLobbyInfo;
    }
}
