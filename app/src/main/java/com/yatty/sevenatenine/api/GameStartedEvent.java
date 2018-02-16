package com.yatty.sevenatenine.api;

import android.os.Handler;

public class GameStartedEvent implements CommandInterface {
    public static final String COMMAND_TYPE = "GameStartedEvent";
    public static final int COMMAND_CODE = 3;
    public final String _type = COMMAND_TYPE;
    private int card;

    @Override
    public void doLogic(Handler handler) {

    }

    public int getCard() {
        return card;
    }
}
