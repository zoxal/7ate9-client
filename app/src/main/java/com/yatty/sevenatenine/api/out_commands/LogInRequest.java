package com.yatty.sevenatenine.api.out_commands;

import java.io.Serializable;

public class LogInRequest implements Serializable {
    private String name;
    private String passwordHash;

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
