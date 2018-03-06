package com.yatty.sevenatenine.api.commands_with_data;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private int value;
    private int modifier;

    public Card(int value, int modifier) {
        this.value = value;
        this.modifier = modifier;
    }

    protected Card(Parcel in) {
        value = in.readInt();
        modifier = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public int getValue() {
        return value;
    }

    public int getModifier() {
        return modifier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeInt(modifier);
    }
}
