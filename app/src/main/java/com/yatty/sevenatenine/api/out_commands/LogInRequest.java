package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LogInRequest implements Serializable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }
}
