package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yatty.sevenatenine.client.Constants;

public class MoveRejectedResponse implements CommandInterface {
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public final String _type = COMMAND_TYPE;
    private Card card;

    @Override
    public void doLogic(Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.REJECTED_CARD_KEY, card);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
