package com.yatty.sevenatenine.api.out_commands;

public class LobbyListUnsubscribeRequest {
    private String authToken;

    public LobbyListUnsubscribeRequest(String authToken) {
        this.authToken = authToken;
    }
}
