package com.yatty.sevenatenine.api;

import android.os.Handler;

public class MoveRequest implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRequest";
    public static final int COMMAND_CODE =5;
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private int move;

    @Override
    public void doLogic(Handler handler) {

    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }
}
