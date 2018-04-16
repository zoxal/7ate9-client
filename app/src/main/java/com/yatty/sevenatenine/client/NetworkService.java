package com.yatty.sevenatenine.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService extends Service {
    private static final String KEY_ACTION = "key_action";
    private static final String KEY_IP = "key_ip";
    private static final String KEY_PORT = "key_port";
    private static final String KEY_MESSAGE = "key_out_message";
    private static final String KEY_KEEP_ALIVE = "key_keep_alive";
    private static final int ACTION_CONNECT = 1;
    private static final int ACTION_SEND_MESSAGE = 2;
    private static final int ACTION_DISCONNECT = 99;

    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(KEY_ACTION, -1);
        switch (action) {
            case ACTION_CONNECT:
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        String ip = intent.getStringExtra(KEY_IP);
                        int port = intent.getIntExtra(KEY_PORT, -1);
                        NettyClient nettyClient = NettyClient.getInstance();
                        nettyClient.setServerIp(ip);
                        nettyClient.setPort(port);
                    }
                });
                break;
            case ACTION_SEND_MESSAGE:
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Serializable message = intent.getSerializableExtra(KEY_MESSAGE);
                        boolean keepAlive = intent.getBooleanExtra(KEY_KEEP_ALIVE, false);
                        NettyClient nettyClient = NettyClient.getInstance();
                        nettyClient.write(message, keepAlive);
                    }
                });
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    public static Intent getConnectionIntent(Context context, String ip, int port) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_CONNECT);
        intent.putExtra(KEY_IP, ip);
        intent.putExtra(KEY_PORT, port);
        return intent;
    }

    public static Intent getSendIntent(Context context, Serializable serializable, boolean keepAlive) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(KEY_ACTION, ACTION_SEND_MESSAGE);
        intent.putExtra(KEY_MESSAGE, serializable);
        intent.putExtra(KEY_KEEP_ALIVE, keepAlive);
        return intent;
    }
}
