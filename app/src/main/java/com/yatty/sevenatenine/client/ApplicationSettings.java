package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.Switch;

public class ApplicationSettings extends AppCompatActivity {
    public static final String MUSIC_VOLUME_KEY = "music_volume";
    public static final String EFFECTS_VOLUME_KEY = "music_volume";
    private SeekBar mMusicSeekBar;
    private SeekBar mEffectsSeekBar;
    private Switch mEnMusicSwitch;
    private Switch mEnEffectsSwitch;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public ApplicationSettings() {
    }

    public int getMusicVolume(AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPreferences = appCompatActivity.getPreferences(Context.MODE_PRIVATE);
        int musicVolume = sharedPreferences.getInt(MUSIC_VOLUME_KEY, 0);
        return musicVolume;
    }

    public boolean isMusicEnabled(AppCompatActivity appCompatActivity) {
        SharedPreferences sharedPreferences = appCompatActivity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(appCompatActivity.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }

    public int loadEffectsVolume() {
        return prefs.getInt(EFFECTS_VOLUME_KEY, 100);
    }

    public boolean loadisMusicVolumeOn() {
        return prefs.getBoolean("enMusicVolume", true);
    }

    public boolean loadisEffectsVolumeOn() {
        return prefs.getBoolean("enEffectsVolume", true);
    }

    public void saveMusicVolume(SeekBar mBar) {
        editor.putInt(MUSIC_VOLUME_KEY, mBar.getProgress());
        editor.commit();
    }

    public void saveEffectsVolume(SeekBar eBar) {
        editor.putInt(EFFECTS_VOLUME_KEY, eBar.getProgress());
        editor.commit();
    }

    public void saveisMusicVolumeOn(boolean on) {
        editor.putBoolean("enMusicVolume", on);
        editor.commit();
    }

    public void saveisEffectsVolumeOn(boolean on) {
        editor.putBoolean("enEffectsVolume", on);
        editor.commit();
    }
}
