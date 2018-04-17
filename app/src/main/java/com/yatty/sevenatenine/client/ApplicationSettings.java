package com.yatty.sevenatenine.client;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class ApplicationSettings {

    public static boolean isMusicEnabled(AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        return sharedPreferences.getBoolean(appCompatActivity.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }

    //TODO: test this
    public static Drawable getBackgroundPicture(AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        int drawableId = sharedPreferences.getInt(String.valueOf(R.string.key_background_picture)
                , R.drawable.table);
        return ContextCompat.getDrawable(appCompatActivity, drawableId);
    }
}
