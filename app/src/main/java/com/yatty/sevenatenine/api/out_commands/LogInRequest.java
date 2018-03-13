package com.yatty.sevenatenine.api.out_commands;

public class LogInRequest {
    public static final String COMMAND_TYPE = "LogInRequest";
    //public final String _type = COMMAND_TYPE;
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
