package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class CreateLobbyResponse implements Parcelable, InCommandInterface {
    private String lobbyId;

    protected CreateLobbyResponse(Parcel in) {
        lobbyId = in.readString();
    }

    @Override
    public void doLogic(Handler handler) {
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
        dest.writeString(lobbyId);
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public static final Creator<CreateLobbyResponse> CREATOR = new Creator<CreateLobbyResponse>() {
        @Override
        public CreateLobbyResponse createFromParcel(Parcel in) {
            return new CreateLobbyResponse(in);
        }

        @Override
        public CreateLobbyResponse[] newArray(int size) {
            return new CreateLobbyResponse[size];
        }
    };
}
