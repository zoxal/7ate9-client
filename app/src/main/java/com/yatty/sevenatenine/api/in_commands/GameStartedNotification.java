package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.yatty.sevenatenine.api.commands_with_data.Card;

import java.util.ArrayList;
import java.util.List;

public class GameStartedNotification implements InCommandInterface {
    public static final String TAG = GameStartedNotification.class.getSimpleName();

    private Card firstCard;
    private List<Card> playerCards;
    private String lobbyId;

    protected GameStartedNotification(Parcel in) {
        firstCard = in.readParcelable(Card.class.getClassLoader());
        playerCards = in.createTypedArrayList(Card.CREATOR);
        lobbyId = in.readString();
    }

    public static final Parcelable.Creator<GameStartedNotification> CREATOR = new Parcelable.Creator<GameStartedNotification>() {
        @Override
        public GameStartedNotification createFromParcel(Parcel in) {
            return new GameStartedNotification(in);
        }

        @Override
        public GameStartedNotification[] newArray(int size) {
            return new GameStartedNotification[size];
        }
    };

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "GameStartedNotification.doLogic");
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
