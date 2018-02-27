package com.yatty.sevenatenine.api;

import android.os.Handler;

import java.io.Serializable;

public class GameResult implements CommandInterface, Serializable {
    public static final String COMMAND_TYPE = "GameResult";
    public final String _type = COMMAND_TYPE;
    private String winner;
    private PlayerResult scores[];

    public String getWinner() {
        return winner;
    }

    public PlayerResult[] getScores() {
        return scores;
    }

    @Override
    public void doLogic(Handler handler) {

    }
}
