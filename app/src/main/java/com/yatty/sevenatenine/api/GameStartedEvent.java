package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yatty.sevenatenine.client.Constants;

public class GameStartedEvent implements CommandInterface {
    public static final String COMMAND_TYPE = "GameStartedEvent";
    public final String _type = COMMAND_TYPE;
    private int card;

    @Override
    public void doLogic(Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.NEXT_CARD_KEY, card);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public int getCard() {
        return card;
    }
}
