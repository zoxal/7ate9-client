package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class ApplicationSettings {
    private static final String TAG = ApplicationSettings.class.getSimpleName();

    private static final String BACKGROUND_PICTURE_TABLE = "wood";
    private static final String BACKGROUND_PICTURE_GRAY = "grey";
    private static final String BACKGROUND_PICTURE_RED = "red";


    public static boolean isMusicEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }

    public static Drawable getBackgroundPicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String backgroundPicture = sharedPreferences.getString(context.getResources()
                .getString(R.string.key_background_picture), BACKGROUND_PICTURE_TABLE);
        Drawable drawable = null;
        if (backgroundPicture.equals(BACKGROUND_PICTURE_TABLE)) {
            Log.d(TAG, BACKGROUND_PICTURE_TABLE);
            drawable = ContextCompat.getDrawable(context, R.drawable.table);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_GRAY)) {
            Log.d(TAG, BACKGROUND_PICTURE_GRAY);
            drawable = ContextCompat.getDrawable(context, R.drawable.shirt);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_RED)) {
            Log.d(TAG, BACKGROUND_PICTURE_RED);
            drawable = ContextCompat.getDrawable(context, R.drawable.b1);
        }
        if (drawable == null) {
            Log.d(TAG, "drawable is null!!!");
        }
        return drawable;
    }
}
