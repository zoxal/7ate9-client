package com.yatty.sevenatenine.api;

import android.os.Handler;
import android.os.Message;

public class MoveRejectedResponse implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private int move;
    private String description;

    @Override
    public void doLogic(Handler handler) {
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        handler.sendMessage(message);

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
