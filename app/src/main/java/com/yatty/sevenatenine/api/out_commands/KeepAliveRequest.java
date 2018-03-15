package com.yatty.sevenatenine.api.out_commands;

public class KeepAliveRequest {
    private String authToken;

    public KeepAliveRequest(String authToken) {
        this.authToken = authToken;
    }
}
