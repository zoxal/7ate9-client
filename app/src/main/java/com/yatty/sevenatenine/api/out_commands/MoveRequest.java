package com.yatty.sevenatenine.api.out_commands;

import com.yatty.sevenatenine.api.commands_with_data.Card;

public class MoveRequest {
    public static final String COMMAND_TYPE = "MoveRequest";
    //public final String _type = COMMAND_TYPE;
    private String gameId;
    private String authToken;
    private int moveNumber;
    private Card move;

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public void setMove(Card move) {
        this.move = move;
    }
}
