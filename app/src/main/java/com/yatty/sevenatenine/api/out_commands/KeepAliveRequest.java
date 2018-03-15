package com.yatty.sevenatenine.api.out_commands;

public class KeepAliveRequest {
    private String authToken;
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
