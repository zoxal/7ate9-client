package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingsActivity extends PreferenceActivity {
    public static final String MUSIC_VOLUME_KEY = "music_volume";
    public static final String EFFECTS_VOLUME_KEY = "music_volume";
    ApplicationSettings appSettings = new ApplicationSettings();
    private SeekBar mMusicSeekBar;
    private SeekBar mEffectsSeekBar;
    private Switch mEnMusicSwitch;
    private Switch mEnEffectsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment()).commit();
        mMusicSeekBar = findViewById(R.id.sb_music_volume);
        mEffectsSeekBar = findViewById(R.id.sb_effects_volume);
        if (appSettings.prefs != null) {
            //mMusicSeekBar.setProgress(appSettings.getMusicVolume(this));
            //mEffectsSeekBar.setProgress(appSettings.loadEffectsVolume());
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        mMusicSeekBar = findViewById(R.id.sb_music_volume);
        appSettings.saveMusicVolume(mMusicSeekBar);
        mEffectsSeekBar = findViewById(R.id.sb_effects_volume);
        appSettings.saveEffectsVolume(mEffectsSeekBar);
        super.onBackPressed();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }
}
