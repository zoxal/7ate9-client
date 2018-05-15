package com.yatty.sevenatenine.client.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.yatty.sevenatenine.client.ApplicationSettings;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService extends Service {
    private static final String TAG = NetworkService.class.getSimpleName();
    private static final String KEY_ACTION = "key_action";
    private static final String KEY_MESSAGE = "key_out_message";
    private static final String KEY_KEEP_ALIVE = "key_keep_alive";
    private static final int ACTION_CONNECT = 1;
    private static final int ACTION_SEND_MESSAGE = 2;
    private static final int ACTION_DISCONNECT = 99;

    private volatile ExecutorService mExecutorService;
    private static volatile Handler responseHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "Start network command");
        int action = intent.getIntExtra(KEY_ACTION, -1);
        switch (action) {
            case ACTION_CONNECT:
                Log.d(TAG, "ACTION_CONNECT");
                mExecutorService.execute(this::connect);
                break;
            case ACTION_SEND_MESSAGE:
                Log.d(TAG, "ACTION_SEND_MESSAGE");
                mExecutorService.execute(() -> {
                    Log.d(TAG, "Trying to send message...");
                    try {
                        Serializable message = intent.getSerializableExtra(KEY_MESSAGE);
                        boolean keepAlive = intent.getBooleanExtra(KEY_KEEP_ALIVE, false);
                        sendMessage(message, keepAlive);
                        Log.d(TAG, "Message sent");
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to send message", e);
                    }
                });
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfo != null && netInfo.isConnected()) || (wifiInfo != null && wifiInfo.isConnected());
    }

    public static Intent getConnectionIntent(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_CONNECT);
        Log.d(TAG, "Intent created");
        return intent;
    }

    public static Intent getSendIntent(Context context,
                                       Serializable message,
                                       boolean keepAlive) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_SEND_MESSAGE);
        intent.putExtra(KEY_MESSAGE, message);
        intent.putExtra(KEY_KEEP_ALIVE, keepAlive);
        return intent;
    }

    public static void setHandler(Handler handler) {
        NettyClient.getInstance().setHandler(handler);
    }

    private void connect() {
        NettyClient nettyClient = NettyClient.getInstance();
        nettyClient.setServerIp(ApplicationSettings.getServerIp(getApplicationContext()));
        nettyClient.setPort(ApplicationSettings.getServerPort(getApplicationContext()));
        nettyClient.connect();
        Log.d(TAG, "Connected");
    }

    private void sendMessage(Object message, boolean keepAlive) {
        NettyClient.getInstance().sendMessage(message, keepAlive);
    }
}
