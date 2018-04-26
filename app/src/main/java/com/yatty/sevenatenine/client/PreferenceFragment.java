package com.yatty.sevenatenine.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class

PreferenceFragment extends android.preference.PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = PreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    // TODO: test this code
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: SharedPreferences key: " + key);
        boolean musicEnabled = ApplicationSettings.isMusicEnabled(getActivity().getApplicationContext());
        if (musicEnabled) {
            Log.i(TAG, "Music enabled");
            BackgroundMusicService.getInstance().start();
        } else {
            Log.i(TAG, "Music disabled");
            BackgroundMusicService.getInstance().pause();
        }
    }
}
