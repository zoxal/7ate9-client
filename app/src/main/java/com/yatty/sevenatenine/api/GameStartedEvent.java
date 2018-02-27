package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yatty.sevenatenine.client.Constants;

import java.util.LinkedList;
import java.util.List;

public class GameStartedEvent implements CommandInterface {
    public static final String TAG = "TAG";
    public static final String COMMAND_TYPE = "GameStartedEvent";
    public final String _type = COMMAND_TYPE;


    private Card firstCard;
    private List<Card> playerCards;

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "GameStartedEvent.doLogic");
        Log.d(TAG, "First card value: " + firstCard.getValue());
        Log.d(TAG, "First card modifier: " + firstCard.getModifier());
        for (int i = 0; i < playerCards.size(); i++) {
            Log.d(TAG, "Gard.value: " + playerCards.get(i).getValue());
            Log.d(TAG, "Gard.modifier: " + playerCards.get(i).getModifier());
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.FIRST_CARD_KEY, firstCard);
        bundle.putSerializable(Constants.CARD_DECK_LIST_KEY, new LinkedList<>(playerCards));
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public Card getFirstCard() {
        return firstCard;
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }
}
