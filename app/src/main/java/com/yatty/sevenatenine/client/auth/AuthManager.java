package com.yatty.sevenatenine.client.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthManager {

    /**
     * Calculates SHA-256 hash based on device MAC.
     *
     * @return  unique device hash
     */
    @SuppressLint("HardwareIds")
    public static String getUniqueDeviceHash(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (manager != null) {
            WifiInfo info = manager.getConnectionInfo();
            return getSHAHash(info.getMacAddress());
        } else {
            throw new RuntimeException("Failed to retrieve WiFiManager");
        }
    }

    /**
     * Calculates SHA-256 hash string for any string.
     *
     * @return  SHA-256 hash
     */
    public static String getSHAHash(@NonNull String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(Charset.forName("UTF-8")));
            return new String(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to find SHA-256 encryption service");
        }
    }
}
