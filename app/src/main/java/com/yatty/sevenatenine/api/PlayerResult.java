package com.yatty.sevenatenine.api;

import java.io.Serializable;

public class PlayerResult implements Serializable {
    public static final String COMMAND_TYPE = "PlayerResult";
    public final String _type = COMMAND_TYPE;
    private String playerName;
    private int cardsLeft;

    public String getPlayerName() {
        return playerName;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }
}
