package com.yatty.sevenatenine.api.out_commands;

public class EnterLobbyRequest {
    private String lobbyId;
    private String authToken;

    public EnterLobbyRequest() {

    }

    public EnterLobbyRequest(String lobbyId, String authToken) {
        this.lobbyId = lobbyId;
        this.authToken = authToken;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
