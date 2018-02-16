package com.yatty.sevenatenine.api;

import android.os.Handler;

public class MoveRejectedResponse implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public static final int COMMAND_CODE = 4;
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private int move;
    private String description;

    @Override
    public void doLogic(Handler handler) {

    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public int getMove() {
        return move;
    }

    public String getDescription() {
        return description;
    }
}
