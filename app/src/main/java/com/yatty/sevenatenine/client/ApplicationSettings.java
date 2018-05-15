package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class ApplicationSettings {
    private static final String TAG = ApplicationSettings.class.getSimpleName();

    private static final String BACKGROUND_PICTURE_CARDS_1 = "cards1";
    private static final String BACKGROUND_PICTURE_CARDS_2 = "cards2";
    private static final String BACKGROUND_PICTURE_CARDS_3 = "cards3";
    private static final String BACKGROUND_PICTURE_WOOD_1 = "wood1";
    private static final String BACKGROUND_PICTURE_WOOD_2 = "wood2";
    private static final String BACKGROUND_PICTURE_WOOD_3 = "wood3";


    public static boolean isMusicEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources()
                .getString(R.string.key_is_music_enabled), true);
    }

    public static boolean areGameSoundsEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources()
                .getString(R.string.key_are_game_sounds_enabled), true);
    }

    public static String getServerIp(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getResources()
                .getString(R.string.key_server_ip), context.getResources()
                .getString(R.string.default_server_ip));
    }

    public static void setServerIp(Context context, String ip) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources()
                .getString(R.string.key_server_ip), ip);
        editor.apply();
    }

    public static int getServerPort(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String portStr = sharedPreferences.getString(context.getResources()
                .getString(R.string.key_server_port), context.getResources()
                .getString(R.string.default_server_port));
        return Integer.valueOf(portStr);
    }

    public static void setServerPort(Context context, int port) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources()
                .getString(R.string.key_server_port), String.valueOf(port));
        editor.apply();
    }

    public static Drawable getBackgroundPicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String backgroundPicture = sharedPreferences.getString(context.getResources()
                .getString(R.string.key_background_picture), BACKGROUND_PICTURE_CARDS_1);
        Drawable drawable = null;
        if (backgroundPicture.equals(BACKGROUND_PICTURE_CARDS_1)) {
            Log.d(TAG, BACKGROUND_PICTURE_CARDS_1);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_cards1);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_CARDS_2)) {
            Log.d(TAG, BACKGROUND_PICTURE_CARDS_2);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_cards2);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_CARDS_3)) {
            Log.d(TAG, BACKGROUND_PICTURE_CARDS_3);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_cards3);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_WOOD_1)) {
            Log.d(TAG, BACKGROUND_PICTURE_WOOD_1);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_dark_brown_wood1);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_WOOD_2)) {
            Log.d(TAG, BACKGROUND_PICTURE_WOOD_2);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_dark_brown_wood2);
        } else if (backgroundPicture.equals(BACKGROUND_PICTURE_WOOD_3)) {
            Log.d(TAG, BACKGROUND_PICTURE_WOOD_3);
            drawable = ContextCompat.getDrawable(context, R.drawable.image_light_wood);
        }
        if (drawable == null) {
            Log.d(TAG, "drawable is null!!!");
        }
        return drawable;
    }
}
