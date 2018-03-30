package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;

import java.util.ArrayList;

public class LobbyListUpdatedNotification implements Parcelable, InCommandInterface {
    private static final String TAG = LobbyListUpdatedNotification.class.getSimpleName();
    private ArrayList<PublicLobbyInfo> lobbyList;


    protected LobbyListUpdatedNotification(Parcel in) {
        lobbyList = in.createTypedArrayList(PublicLobbyInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(lobbyList);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "LobbyList do logic");
        if (lobbyList == null) {
            Log.d(TAG, "lobbyList is null!!!!!!!!!");
        }
        Message message = new Message();
        message.obj = this;
        handler.sendMessage(message);
    }

    public ArrayList<PublicLobbyInfo> getLobbyList() {
        return lobbyList;
    }

    public static final Creator<LobbyListUpdatedNotification> CREATOR = new Creator<LobbyListUpdatedNotification>() {
        @Override
        public LobbyListUpdatedNotification createFromParcel(Parcel in) {
            return new LobbyListUpdatedNotification(in);
        }

        @Override
        public LobbyListUpdatedNotification[] newArray(int size) {
            return new LobbyListUpdatedNotification[size];
        }
    };
}
