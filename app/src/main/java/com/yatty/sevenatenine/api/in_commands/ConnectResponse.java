package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

public class ConnectResponse implements InCommandInterface {
    public static final int UID = ConnectResponse.class.getSimpleName().hashCode();
    public static final String TAG = ConnectResponse.class.getSimpleName();
    public static final String COMMAND_TYPE = "ConnectResponse";
    public final String _type = COMMAND_TYPE;
    private boolean succeed;
    private String description;
    private String gameId;

    protected ConnectResponse(Parcel in) {
        succeed = in.readByte() != 0;
        description = in.readString();
        gameId = in.readString();
    }

    public static final Creator<ConnectResponse> CREATOR = new Creator<ConnectResponse>() {
        @Override
        public ConnectResponse createFromParcel(Parcel in) {
            return new ConnectResponse(in);
        }

        @Override
        public ConnectResponse[] newArray(int size) {
            return new ConnectResponse[size];
        }
    };

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "ConnectResponse.doLogic");
        Message message = new Message();
        message.obj = this;
        message.sendingUid = UID;
        handler.sendMessage(message);
    }

    public boolean isSucceed() {
        return succeed;
    }

    public String getDescription() {
        return description;
    }

    public String getGameId() {
        return gameId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (succeed ? 1 : 0));
        dest.writeString(description);
        dest.writeString(gameId);
    }
}
