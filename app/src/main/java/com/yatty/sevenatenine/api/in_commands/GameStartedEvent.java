package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

import com.yatty.sevenatenine.api.commands_with_data.Card;

import java.util.ArrayList;
import java.util.List;

public class GameStartedEvent implements InCommandInterface {
    public static final String TAG = GameStartedEvent.class.getSimpleName();

    private Card firstCard;
    private List<Card> playerCards;
    private String lobbyId;

    protected GameStartedEvent(Parcel in) {
        firstCard = in.readParcelable(Card.class.getClassLoader());
        playerCards = in.createTypedArrayList(Card.CREATOR);
        lobbyId = in.readString();
    }

    public static final Creator<GameStartedEvent> CREATOR = new Creator<GameStartedEvent>() {
        @Override
        public GameStartedEvent createFromParcel(Parcel in) {
            return new GameStartedEvent(in);
        }

        @Override
        public GameStartedEvent[] newArray(int size) {
            return new GameStartedEvent[size];
        }
    };

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "GameStartedEvent.doLogic");
        Log.d(TAG, "First card value: " + firstCard.getValue());
        Log.d(TAG, "First card modifier: " + firstCard.getModifier());
        Log.d(TAG, "LobbyId: " + lobbyId);
        Message message = new Message();
        message.obj = this;
        handler.sendMessage(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(firstCard, flags);
        dest.writeTypedList(playerCards);
        dest.writeString(lobbyId);
    }

    public Card getFirstCard() {
        return firstCard;
    }

    public ArrayList<Card> getPlayerCards() {
        return (ArrayList<Card>) playerCards;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
