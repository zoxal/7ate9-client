package com.yatty.sevenatenine.client;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class ApplicationSettings {

    public boolean isMusicEnabled(AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        return sharedPreferences.getBoolean(appCompatActivity.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }
}
