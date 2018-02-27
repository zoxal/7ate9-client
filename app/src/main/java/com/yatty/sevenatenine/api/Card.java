package com.yatty.sevenatenine.api;

import java.io.Serializable;

public class Card implements Serializable {
    private int value;
    private int modifier;

    public Card(int value, int modifier) {
        this.value = value;
        this.modifier = modifier;
    }

    public int getValue() {
        return value;
    }

    public int getModifier() {
        return modifier;
    }
}
