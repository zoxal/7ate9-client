package com.yatty.sevenatenine.api.out_commands;

public class CreateLobbyRequest {
    private String lobbyName;
    private int maxPlayersNumber;
    private String authToken;
    
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
