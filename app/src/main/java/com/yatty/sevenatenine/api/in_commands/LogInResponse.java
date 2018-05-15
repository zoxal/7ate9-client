package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;

public class LogInResponse implements InCommandInterface {
    public static final String TAG = LogInResponse.class.getSimpleName();
    private String authToken;
    private String playerId;
    private int rating;

    protected LogInResponse(Parcel in) {
        authToken = in.readString();
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
        dest.writeString(authToken);
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getRating() {
        return rating;
    }

    public static final Creator<LogInResponse> CREATOR = new Creator<LogInResponse>() {
        @Override
        public LogInResponse createFromParcel(Parcel in) {
            return new LogInResponse(in);
        }

        @Override
        public LogInResponse[] newArray(int size) {
            return new LogInResponse[size];
        }
    };
}
