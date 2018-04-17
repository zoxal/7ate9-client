package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class GameResult implements Parcelable, Serializable {
    private String winner;
    private PlayerResult scores[];

    protected GameResult(Parcel in) {
        winner = in.readString();
        scores = in.createTypedArray(PlayerResult.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(winner);
        dest.writeTypedArray(scores, 0);
    }

    public String getWinner() {
        return winner;
    }

    public PlayerResult[] getScores() {
        return scores;
    }

    public static final Creator<GameResult> CREATOR = new Creator<GameResult>() {
        @Override
        public GameResult createFromParcel(Parcel in) {
            return new GameResult(in);
        }

        @Override
        public GameResult[] newArray(int size) {
            return new GameResult[size];
        }
    };

}
