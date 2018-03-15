package com.yatty.sevenatenine.api.out_commands;

public class LobbyUnsubscribeRequest {
    private String authToken;

    public LobbyUnsubscribeRequest(String authToken) {
        this.authToken = authToken;
    }
}
