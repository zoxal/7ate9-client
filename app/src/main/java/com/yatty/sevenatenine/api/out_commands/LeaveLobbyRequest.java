package com.yatty.sevenatenine.api.out_commands;

public class LeaveLobbyRequest {
    private String lobbyId;
    private String authToken;

    public LeaveLobbyRequest(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
