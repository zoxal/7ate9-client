package com.yatty.sevenatenine.api;

import android.os.Handler;

public class DisconnectRequest implements CommandInterface {
    public static final String COMMAND_TYPE = "DisconnectRequest";
    public final String _type = COMMAND_TYPE;
    private String gameId;

    @Override
    public void doLogic(Handler handler) {
    }

    public String getGameId() {
        return gameId;
    }
}
