package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yatty.sevenatenine.client.Constants;

public class ConnectResponse implements CommandInterface {
    public static final String TAG = "TAG";
    public static final String COMMAND_TYPE = "ConnectResponse";
    public final String _type = COMMAND_TYPE;
    private boolean succeed;
    private String description;
    private String gameId;

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "in doLogic, ConnectResponse");
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.GAME_ID_KEY, gameId);
        bundle.putBoolean(Constants.IS_CONNECT_SUCCEED_KEY, succeed);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
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
}
