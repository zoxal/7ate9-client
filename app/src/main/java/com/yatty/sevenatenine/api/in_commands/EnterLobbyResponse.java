package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;

public class EnterLobbyResponse implements Parcelable, InCommandInterface {
    private PrivateLobbyInfo privateLobbyInfo;

    protected EnterLobbyResponse(Parcel in) {
        privateLobbyInfo = in.readParcelable(PrivateLobbyInfo.class.getClassLoader());
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
        dest.writeParcelable(privateLobbyInfo, flags);
    }

    public PrivateLobbyInfo getPrivateLobbyInfo() {
        return privateLobbyInfo;
    }

    public static final Creator<EnterLobbyResponse> CREATOR = new Creator<EnterLobbyResponse>() {
        @Override
        public EnterLobbyResponse createFromParcel(Parcel in) {
            return new EnterLobbyResponse(in);
        }

        @Override
        public EnterLobbyResponse[] newArray(int size) {
            return new EnterLobbyResponse[size];
        }
    };
}
