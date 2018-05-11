package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PrivateLobbyInfo implements Parcelable, Serializable {
    private PlayerInfo players[];

    public PrivateLobbyInfo(PlayerInfo playerInfo) {
        players = new PlayerInfo[1];
        players[0] = playerInfo;
    }

    protected PrivateLobbyInfo(Parcel in) {
        players = in.createTypedArray(PlayerInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(players, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public PlayerInfo[] getPlayers() {
        return players;
    }

    public static final Creator<PrivateLobbyInfo> CREATOR = new Creator<PrivateLobbyInfo>() {
        @Override
        public PrivateLobbyInfo createFromParcel(Parcel in) {
            return new PrivateLobbyInfo(in);
        }

        @Override
        public PrivateLobbyInfo[] newArray(int size) {
            return new PrivateLobbyInfo[size];
        }
    };
}
