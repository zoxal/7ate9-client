package com.yatty.sevenatenine.api.out_commands;

import android.os.Handler;

public class ConnectRequest {
    public static final String COMMAND_TYPE = "ConnectRequest";
    public final String _type = COMMAND_TYPE;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
