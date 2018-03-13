package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerResult implements Parcelable {
    public static final String COMMAND_TYPE = "PlayerResult";
    //public final String _type = COMMAND_TYPE;
    private String playerName;
    private int cardsLeft;

    protected PlayerResult(Parcel in) {
        playerName = in.readString();
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
        dest.writeString(playerName);
        dest.writeInt(cardsLeft);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

}
