package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yatty.sevenatenine.client.Constants;

public class MoveRejectedResponse implements CommandInterface {
    public static final String TAG = "TAG";
    public static final String COMMAND_TYPE = "MoveRejectedResponse";
    public final String _type = COMMAND_TYPE;
    private Card move;

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "MoveRejectedResponse.doLogic");
        Log.d(TAG, "Rejected move");
        Log.d(TAG, "value: " + move.getValue());
        Log.d(TAG, "modifier: " + move.getModifier());
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.REJECTED_CARD_KEY, move);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
