package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LeaveGameRequest implements Serializable {
    private String gameId;
    private String authToken;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
