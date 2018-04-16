package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LogOutRequest implements Serializable {
    private String authToken;

    public LogOutRequest() {
    }

    public LogOutRequest(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
