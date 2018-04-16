package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LobbyListSubscribeRequest  implements Serializable {
    private String authToken;

    public LobbyListSubscribeRequest() {
    }

    public LobbyListSubscribeRequest(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
