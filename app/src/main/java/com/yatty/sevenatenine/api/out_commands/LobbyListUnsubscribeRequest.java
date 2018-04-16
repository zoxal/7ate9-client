package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LobbyListUnsubscribeRequest implements Serializable {
    private String authToken;

    public LobbyListUnsubscribeRequest(String authToken) {
        this.authToken = authToken;
    }
}
