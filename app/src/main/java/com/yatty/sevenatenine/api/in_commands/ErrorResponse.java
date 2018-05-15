package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

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
        Message message = new Message();
        message.obj = this;
        handler.sendMessage(message);
    }

    public String getErrorUUID() {
        return errorUUID;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public static final Parcelable.Creator<ErrorResponse> CREATOR = new Parcelable.Creator<ErrorResponse>() {
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
