package com.yatty.sevenatenine.api.out_commands;

public class LogOutRequest {
    public static final String COMMAND_TYPE = "LogOutRequest";
    //public final String _type = COMMAND_TYPE;
    private String gameId;

    public LogOutRequest(String gameId) {
        this.gameId = gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
