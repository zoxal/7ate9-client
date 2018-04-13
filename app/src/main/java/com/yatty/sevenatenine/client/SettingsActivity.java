package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.widget.SeekBar;
import android.widget.Switch;
import java.util.Map;
import java.util.Set;

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
        if (appSettings.prefs!= null) {
            mMusicSeekBar.setProgress(appSettings.loadMusicVolume());
            mEffectsSeekBar.setProgress(appSettings.loadEffectsVolume());
        }
        else{
            appSettings.prefs = new SharedPreferences() {
                @Override
                public Map<String, ?> getAll() {
                    return null;
                }

                @Nullable
                @Override
                public String getString(String key, @Nullable String defValue) {
                    return null;
                }

                @Nullable
                @Override
                public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
                    return null;
                }

                @Override
                public int getInt(String key, int defValue) {
                    return 0;
                }

                @Override
                public long getLong(String key, long defValue) {
                    return 0;
                }

                @Override
                public float getFloat(String key, float defValue) {
                    return 0;
                }

                @Override
                public boolean getBoolean(String key, boolean defValue) {
                    return false;
                }

                @Override
                public boolean contains(String key) {
                    return false;
                }

                @Override
                public Editor edit() {
                    return null;
                }

                @Override
                public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

                }

                @Override
                public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

                }
            };
            appSettings.editor = new SharedPreferences.Editor() {
                @Override
                public SharedPreferences.Editor putString(String key, @Nullable String value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putInt(String key, int value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putLong(String key, long value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putFloat(String key, float value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putBoolean(String key, boolean value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor remove(String key) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor clear() {
                    return null;
                }

                @Override
                public boolean commit() {
                    return false;
                }

                @Override
                public void apply() {

                }
            };
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
