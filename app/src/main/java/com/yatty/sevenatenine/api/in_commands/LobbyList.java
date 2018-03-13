package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;

public class LobbyList implements Parcelable, InCommandInterface {
    private PublicLobbyInfo lobbyList[];


    protected LobbyList(Parcel in) {
        lobbyList = in.createTypedArray(PublicLobbyInfo.CREATOR);
    }

    @Override
    public void doLogic(Handler handler) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(lobbyList, flags);
    }

    public static final Creator<LobbyList> CREATOR = new Creator<LobbyList>() {
        @Override
        public LobbyList createFromParcel(Parcel in) {
            return new LobbyList(in);
        }

        @Override
        public LobbyList[] newArray(int size) {
            return new LobbyList[size];
        }
    };
}
