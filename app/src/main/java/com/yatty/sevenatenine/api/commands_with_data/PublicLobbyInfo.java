package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

public class PublicLobbyInfo implements Parcelable {
    private String lobbyId;
    private String lobbyName;
    private int maxPlayersNumber;
    private int currentPlayersNumber;

    public PublicLobbyInfo() {

    }

    protected PublicLobbyInfo(Parcel in) {
        lobbyId = in.readString();
        lobbyName = in.readString();
        maxPlayersNumber = in.readInt();
        currentPlayersNumber = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lobbyId);
        dest.writeString(lobbyName);
        dest.writeInt(maxPlayersNumber);
        dest.writeInt(currentPlayersNumber);
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

    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }

    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
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
