package com.yatty.sevenatenine.api;

public class MoveRequest {
    public static final String COMMAND_TYPE = "MoveRequest";
    public final String _type = COMMAND_TYPE;
    private String gameId;
    private int moveNumber;
    private Card move;

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public void setMove(Card move) {
        this.move = move;
    }
}
