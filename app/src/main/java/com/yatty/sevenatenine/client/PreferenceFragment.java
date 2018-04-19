package com.yatty.sevenatenine.client;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class

PreferenceFragment extends android.preference.PreferenceFragment {

    public static final String TAG = "PreferenceFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    // TODO: test deleting this code
    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "Music settings changed. SharedPreferences key: " + key);
        musicEnabled = sharedPreferences.getBoolean(key, true);
        if (musicEnabled) {
            Log.i(TAG, "Music enabled");
            BackgroundMusicService.getInstance().start();
        } else {
            Log.i(TAG, "Music disabled");
            BackgroundMusicService.getInstance().pause();
        }
    }*/
}
