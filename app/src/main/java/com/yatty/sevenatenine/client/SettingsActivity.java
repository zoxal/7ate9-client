package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {
    public static final String MUSIC_VOLUME_KEY = "music_volume";
    public static final String EFFECTS_VOLUME_KEY = "effects_volume";
    ApplicationSettings appSettings = new ApplicationSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicService.getInstance(this.getApplicationContext()).pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(ApplicationSettings.getBackgroundPicture(getApplicationContext()));
        if (ApplicationSettings.isMusicEnabled(this)) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).start();
        }
    }

}
