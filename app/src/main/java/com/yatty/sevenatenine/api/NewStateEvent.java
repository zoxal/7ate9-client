package com.yatty.sevenatenine.api;

import android.os.Handler;

public class NewStateEvent implements CommandInterface {
    public static final String COMMAND_TYPE = "NewStateEvent";
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private int move;
    private String player;
    private boolean lastMove;
    private int nextCard;

    @Override
    public void doLogic(Handler handler) {

    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public int getMove() {
        return move;
    }

    public String getPlayer() {
        return player;
    }

    public boolean isLastMove() {
        return lastMove;
    }

    public int getNextCard() {
        return nextCard;
    }
}
