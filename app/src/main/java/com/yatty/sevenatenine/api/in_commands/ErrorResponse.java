package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Parcel;

public class ErrorResponse implements InCommandInterface {
    private String errorUUID;
    private String shortDescription;
    private String additionalInfo;

    protected ErrorResponse(Parcel in) {
        errorUUID = in.readString();
        shortDescription = in.readString();
        additionalInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorUUID);
        dest.writeString(shortDescription);
        dest.writeString(additionalInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void doLogic(Handler handler) {

    }

    public static final Creator<ErrorResponse> CREATOR = new Creator<ErrorResponse>() {
        @Override
        public ErrorResponse createFromParcel(Parcel in) {
            return new ErrorResponse(in);
        }

        @Override
        public ErrorResponse[] newArray(int size) {
            return new ErrorResponse[size];
        }
    };
}
