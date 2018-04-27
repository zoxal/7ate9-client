package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PlayerResult implements Parcelable, Serializable {
    private String playerId;
    private int cardsLeft;

    protected PlayerResult(Parcel in) {
        playerId = in.readString();
        cardsLeft = in.readInt();
    }

    public static final Creator<PlayerResult> CREATOR = new Creator<PlayerResult>() {
        @Override
        public PlayerResult createFromParcel(Parcel in) {
            return new PlayerResult(in);
        }

        @Override
        public PlayerResult[] newArray(int size) {
            return new PlayerResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerId);
        dest.writeInt(cardsLeft);
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

}
