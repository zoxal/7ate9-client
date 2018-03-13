package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;

public class LogInResponse implements InCommandInterface {
    public static final int UID = LogInResponse.class.getSimpleName().hashCode();
    public static final String TAG = LogInResponse.class.getSimpleName();
    public static final String COMMAND_TYPE = "LogInResponse";
    //public final String _type = COMMAND_TYPE;
    private String authToken;

    protected LogInResponse(Parcel in) {
        authToken = in.readString();
    }

    @Override
    public void doLogic(Handler handler) {
        Message message = new Message();
        message.obj = this;
        message.sendingUid = UID;
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
