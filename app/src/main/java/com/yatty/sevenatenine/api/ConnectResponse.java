package com.yatty.sevenatenine.api;

import android.os.Handler;

public class ConnectResponse implements CommandInterface {
    public static final String COMMAND_TYPE = "ConnectResponse";
    public static final int COMMAND_CODE = 2;
    public final String _type = COMMAND_TYPE;
    private boolean succeed;
    private String description;

    @Override
    public void doLogic(Handler handler) {
    }

    public boolean isSucceed() {
        return succeed;
    }

    public String getDescription() {
        return description;
    }
}
