package com.yatty.sevenatenine.api.out_commands;

public class LogOutRequest {
    private String gameId;

    public LogOutRequest(String gameId) {
        this.gameId = gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
