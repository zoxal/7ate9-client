package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

public class PublicLobbyInfo implements Parcelable {
    private String lobbyId;
    private String lobbyName;
    private int maxNumberOfPlayers;
    private int currentNumberOfPlayers;

    protected PublicLobbyInfo(Parcel in) {
        lobbyId = in.readString();
        lobbyName = in.readString();
        maxNumberOfPlayers = in.readInt();
        currentNumberOfPlayers = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lobbyId);
        dest.writeString(lobbyName);
        dest.writeInt(maxNumberOfPlayers);
        dest.writeInt(currentNumberOfPlayers);
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public int getCurrentNumberOfPlayers() {
        return currentNumberOfPlayers;
    }

    public void setCurrentNumberOfPlayers(int currentNumberOfPlayers) {
        this.currentNumberOfPlayers = currentNumberOfPlayers;
    }

    public static final Creator<PublicLobbyInfo> CREATOR = new Creator<PublicLobbyInfo>() {
        @Override
        public PublicLobbyInfo createFromParcel(Parcel in) {
            return new PublicLobbyInfo(in);
        }

        @Override
        public PublicLobbyInfo[] newArray(int size) {
            return new PublicLobbyInfo[size];
        }
    };
}
