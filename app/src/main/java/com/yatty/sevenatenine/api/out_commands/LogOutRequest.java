package com.yatty.sevenatenine.api.out_commands;

public class LogOutRequest {
    private String authToken;

    public LogOutRequest(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
