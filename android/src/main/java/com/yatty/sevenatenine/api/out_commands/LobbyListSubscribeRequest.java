package com.yatty.sevenatenine.api.out_commands;

public class LobbyListSubscribeRequest {
    private String authToken;

    public LobbyListSubscribeRequest() {
    }

    public LobbyListSubscribeRequest(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
