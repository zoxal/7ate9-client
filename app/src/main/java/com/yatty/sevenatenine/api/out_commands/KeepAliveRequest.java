package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class KeepAliveRequest implements Serializable {
    private String authToken;

    public KeepAliveRequest(String authToken) {
        this.authToken = authToken;
    }
}
