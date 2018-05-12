package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PlayerInfo implements Parcelable, Serializable {
    private String playerId;
    private int rating;

    public PlayerInfo(String playerId) {
        this.playerId = playerId;
    }

    protected PlayerInfo(Parcel in) {
        playerId = in.readString();
        rating = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerId);
        dest.writeInt(rating);
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getRating() {
        return rating;
    }


    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };
}
