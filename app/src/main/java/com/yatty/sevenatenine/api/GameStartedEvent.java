package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yatty.sevenatenine.client.Constants;

public class GameStartedEvent implements CommandInterface {
    public static final String COMMAND_TYPE = "GameStartedEvent";
    public final String _type = COMMAND_TYPE;
    private Card firstCard;
    private Card cards[];

    @Override
    public void doLogic(Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.FIRST_CARD_KEY, firstCard);
        bundle.putSerializable(Constants.CARD_DECK_KEY, cards);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public Card getFirstCard() {
        return firstCard;
    }

    public Card[] getCards() {
        return cards;
    }
}
