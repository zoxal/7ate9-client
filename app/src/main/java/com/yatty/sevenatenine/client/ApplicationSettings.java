package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

public class ApplicationSettings {

    public static boolean isMusicEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }

    //TODO: test this
    public static Drawable getBackgroundPicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int drawableId = sharedPreferences.getInt(String.valueOf(R.string.key_background_picture)
                , R.drawable.table);
        return ContextCompat.getDrawable(context, drawableId);
    }
}
