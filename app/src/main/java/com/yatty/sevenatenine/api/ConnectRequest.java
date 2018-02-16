package com.yatty.sevenatenine.api;

import android.os.Handler;

public class ConnectRequest implements CommandInterface {
    public static final String COMMAND_TYPE = "ConnectRequest";
    public final String _type = COMMAND_TYPE;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void doLogic(Handler handler) {

    }
}
