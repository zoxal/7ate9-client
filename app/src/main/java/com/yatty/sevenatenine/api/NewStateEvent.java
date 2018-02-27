package com.yatty.sevenatenine.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yatty.sevenatenine.client.Constants;

public class NewStateEvent implements CommandInterface {
    public static final String COMMAND_TYPE = "NewStateEvent";
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private String moveWinner;
    private boolean lastMove;
    private Card nextCard;
    private GameResult gameResult;

    @Override
    public void doLogic(Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PLAYER_WITH_RIGHT_ANSWER_KEY, moveWinner);
        bundle.putSerializable(Constants.NEXT_CARD_KEY, nextCard);
        bundle.putSerializable(Constants.IS_LAST_MOVE_KEY, lastMove);
        bundle.putSerializable(Constants.MOVE_NUMBER_KEY, moveNumber);
        bundle.putSerializable(Constants.GAME_RESULT_KEY, gameResult);
        Message message = new Message();
        message.obj = COMMAND_TYPE;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getMoveWinner() {
        return moveWinner;
    }

    public boolean isLastMove() {
        return lastMove;
    }

    public Card getNextCard() {
        return nextCard;
    }

    public GameResult getGameResult() {
        return gameResult;
    }
}
