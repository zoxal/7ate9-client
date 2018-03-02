package com.yatty.sevenatenine.api.in_commands;

import android.os.Handler;
import android.os.Parcelable;

/**
 * This interface should be implemented by Commands, which could be received.
 */

public interface InCommandInterface extends Parcelable{
    void doLogic(Handler handler);
}
