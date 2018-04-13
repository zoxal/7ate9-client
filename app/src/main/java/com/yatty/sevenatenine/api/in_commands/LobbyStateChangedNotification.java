package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;

public class LobbyStateChangedNotification implements Parcelable, InCommandInterface {
    private PrivateLobbyInfo privateLobbyInfo;


    protected LobbyStateChangedNotification(Parcel in) {
        privateLobbyInfo = in.readParcelable(PrivateLobbyInfo.class.getClassLoader());
    }

    @Override
    public void doLogic(Handler handler) {
        Message message = new Message();
        message.obj = this;
        handler.sendMessage(message);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(privateLobbyInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LobbyStateChangedNotification> CREATOR = new Creator<LobbyStateChangedNotification>() {
        @Override
        public LobbyStateChangedNotification createFromParcel(Parcel in) {
            return new LobbyStateChangedNotification(in);
        }

        @Override
        public LobbyStateChangedNotification[] newArray(int size) {
            return new LobbyStateChangedNotification[size];
        }
    };

    public PrivateLobbyInfo getPrivateLobbyInfo() {
        return privateLobbyInfo;
    }

}
