package com.yatty.sevenatenine.api.out_commands;

import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;

public class CreateLobbyRequest {
    private PublicLobbyInfo publicLobbyInfo;
    private String authToken;

    public void setPublicLobbyInfo(PublicLobbyInfo publicLobbyInfo) {
        this.publicLobbyInfo = publicLobbyInfo;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
