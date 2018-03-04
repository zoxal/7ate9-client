package com.yatty.sevenatenine.api;

import java.io.Serializable;

public class PlayerResult implements Serializable {
    public static final String COMMAND_TYPE = "PlayerResult";
    public final String _type = COMMAND_TYPE;
    private String playerName;
    private int cardsLeft;

    public PlayerResult(String name,int cLeft){
        this.playerName = name;
        this.cardsLeft = cLeft;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

    public void sortByCardCount(PlayerResult[] res) {
        PlayerResult tmp;
        for (int i = 0; i < res.length; i++)
            for (int j = i + 1; j < res.length; j++)
                if (res[j].getCardsLeft() < res[i].getCardsLeft()) {
                    tmp = res[i];
                    res[i] = res[j];
                    res[j] = tmp;
                }

    }
}
