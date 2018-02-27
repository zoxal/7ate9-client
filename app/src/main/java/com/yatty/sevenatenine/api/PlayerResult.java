package com.yatty.sevenatenine.api;

class PlayerResult {
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
