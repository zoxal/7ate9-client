package com.yatty.sevenatenine.api.out_commands;

public class LobbySubscribeRequest {
    private String authToken;

    public LobbySubscribeRequest() {
    }

    public LobbySubscribeRequest(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
