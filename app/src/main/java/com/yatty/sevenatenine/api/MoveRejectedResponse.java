package com.yatty.sevenatenine.api;

import android.os.Handler;
import android.os.Message;

public class MoveRejectedResponse implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public final String _type = COMMAND_TYPE;
    private Card card;

    @Override
    public void doLogic(Handler handler) {
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        handler.sendMessage(message);
    }
}
