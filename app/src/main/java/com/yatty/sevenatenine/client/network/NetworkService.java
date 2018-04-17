package com.yatty.sevenatenine.client.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

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

    private static String sIp = "192.168.100.4";
    private static int sPort = 39405;

    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        int action = intent.getIntExtra(KEY_ACTION, -1);
        switch (action) {
            case ACTION_CONNECT:
                Log.d(TAG, "ACTION_CONNECT");
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        NettyClient nettyClient = NettyClient.getInstance();
                        nettyClient.setServerIp(sIp);
                        nettyClient.setPort(sPort);
                        nettyClient.connect();
                        Log.d(TAG, "Connected");
                    }
                });
                break;
            case ACTION_SEND_MESSAGE:
                Log.d(TAG, "ACTION_SEND_MESSAGE");
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Serializable message = intent.getSerializableExtra(KEY_MESSAGE);
                        boolean keepAlive = intent.getBooleanExtra(KEY_KEEP_ALIVE, false);
                        NettyClient nettyClient = NettyClient.getInstance();
                        nettyClient.write(message, keepAlive);
                        Log.d(TAG, "message was sent");
                    }
                });
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    public static Intent getConnectionIntent(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_CONNECT);
        return intent;
    }

    public static Intent getSendIntent(Context context, Serializable serializable, boolean keepAlive) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_SEND_MESSAGE);
        intent.putExtra(KEY_MESSAGE, serializable);
        intent.putExtra(KEY_KEEP_ALIVE, keepAlive);
        return intent;
    }

    public static String getIp() {
        return sIp;
    }

    public static void setIp(String ip) {
        sIp = ip;
    }

    public static int getPort() {
        return sPort;
    }

    public static void setPort(int port) {
        sPort = port;
    }

    public static void setHandler(Handler handler) {
        NettyClient.getInstance().setHandler(handler);
    }
}
