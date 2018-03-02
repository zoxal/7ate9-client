package com.yatty.sevenatenine.api.in_commands;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

import com.yatty.sevenatenine.api.commands_with_data.Card;

public class MoveRejectedResponse implements InCommandInterface {
    public static final int UID = MoveRejectedResponse.COMMAND_TYPE.hashCode();
    public static final String TAG = "TAG";
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public final String _type = COMMAND_TYPE;
    private Card move;

    protected MoveRejectedResponse(Parcel in) {
        move = in.readParcelable(Card.class.getClassLoader());
    }

    public static final Creator<MoveRejectedResponse> CREATOR = new Creator<MoveRejectedResponse>() {
        @Override
        public MoveRejectedResponse createFromParcel(Parcel in) {
            return new MoveRejectedResponse(in);
        }

        @Override
        public MoveRejectedResponse[] newArray(int size) {
            return new MoveRejectedResponse[size];
        }
    };

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "MoveRejectedResponse.doLogic");
        Log.d(TAG, "Rejected move");
        Log.d(TAG, "value: " + move.getValue());
        Log.d(TAG, "modifier: " + move.getModifier());
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
        dest.writeParcelable(move, flags);
    }

    public Card getMove() {
        return move;
    }
}
