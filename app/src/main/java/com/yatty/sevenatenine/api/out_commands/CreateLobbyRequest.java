package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class CreateLobbyRequest implements Serializable {
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
