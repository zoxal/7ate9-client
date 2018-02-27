package com.yatty.sevenatenine.api;

import android.os.Handler;

public class MoveRequest implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRequest";
    public final String _type = COMMAND_TYPE;
    private String gameId;
    private int moveNumber;
    private Card move;

    @Override
    public void doLogic(Handler handler) {

    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Card getMove() {
        return move;
    }

    public void setMove(Card move) {
        this.move = move;
    }
}
