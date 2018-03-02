package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

import com.yatty.sevenatenine.api.commands_with_data.Card;
import com.yatty.sevenatenine.api.commands_with_data.GameResult;

public class NewStateEvent implements InCommandInterface {
    public static final int UID = NewStateEvent.COMMAND_TYPE.hashCode();
    public static final String TAG = "TAG";
    public static final String COMMAND_TYPE = "NewStateEvent";
    public final String _type = COMMAND_TYPE;
    private int moveNumber;
    private String moveWinner;
    private boolean lastMove;
    private Card nextCard;
    private GameResult gameResult;

    protected NewStateEvent(Parcel in) {
        moveNumber = in.readInt();
        moveWinner = in.readString();
        lastMove = in.readByte() != 0;
        nextCard = in.readParcelable(Card.class.getClassLoader());
        gameResult = in.readParcelable(GameResult.class.getClassLoader());
    }

    public static final Creator<NewStateEvent> CREATOR = new Creator<NewStateEvent>() {
        @Override
        public NewStateEvent createFromParcel(Parcel in) {
            return new NewStateEvent(in);
        }

        @Override
        public NewStateEvent[] newArray(int size) {
            return new NewStateEvent[size];
        }
    };

    @Override
    public void doLogic(Handler handler) {
        Log.d(TAG, "NewStateEvent.doLogic");
        Message message = new Message();
        message.obj = this;
        message.sendingUid = UID;
        handler.sendMessage(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(moveNumber);
        dest.writeString(moveWinner);
        dest.writeByte((byte) (lastMove ? 1 : 0));
        dest.writeParcelable(nextCard, flags);
        dest.writeParcelable(gameResult, flags);
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
