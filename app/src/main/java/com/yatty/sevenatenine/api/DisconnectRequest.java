package com.yatty.sevenatenine.api;

public class DisconnectRequest {
    public static final String COMMAND_TYPE = "DisconnectRequest";
    public final String _type = COMMAND_TYPE;
    private String gameId;

    public DisconnectRequest(String gameId) {
        this.gameId = gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
