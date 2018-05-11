package com.yatty.sevenatenine.client.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yatty.sevenatenine.api.CommandsTypeMapper;
import com.yatty.sevenatenine.api.in_commands.InCommandInterface;
import com.yatty.sevennine.client.SevenAteNineClient;
import com.yatty.sevennine.client.SevenAteNineClientFactory;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.yatty.sevenatenine.api.CommandsTypeMapper.COMMAND_TYPE_FIELD;
import static com.yatty.sevenatenine.api.CommandsTypeMapper.TYPE_FIELD;

public class NetworkService extends Service {
    private static final String TAG = NetworkService.class.getSimpleName();
    private static final String KEY_ACTION = "key_action";
    private static final String KEY_MESSAGE = "key_out_message";
    private static final String KEY_KEEP_ALIVE = "key_keep_alive";
    private static final int ACTION_CONNECT = 1;
    private static final int ACTION_SEND_MESSAGE = 2;
    private static final int ACTION_DISCONNECT = 99;

    private static String sIp = "192.168.0.103";
    private static int sPort = 39405;

    private SevenAteNineClientFactory networkClientFactory;
    private SevenAteNineClient networkClient;
    private volatile ExecutorService mExecutorService;
    private static volatile Handler responseHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newSingleThreadExecutor();
        networkClientFactory = new SevenAteNineClientFactory();
        configureNetworkClientFactory(networkClientFactory);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "Start network command");
//        int action = intent.getIntExtra(KEY_ACTION, -1);
//        switch (action) {
//            case ACTION_CONNECT:
//                Log.d(TAG, "ACTION_CONNECT");
//                mExecutorService.execute(this::connect);
//                break;
//            case ACTION_SEND_MESSAGE:
//                Log.d(TAG, "ACTION_SEND_MESSAGE");
//                mExecutorService.execute(() -> {
//                    Serializable message = intent.getSerializableExtra(KEY_MESSAGE);
//                    boolean keepAlive = intent.getBooleanExtra(KEY_KEEP_ALIVE, false);
//                    sendMessage(message, keepAlive);
//                });
//                break;
//        }
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
        responseHandler = handler;
    }

    private void connect() {
//        try {
//            networkClient = networkClientFactory.getClient(new InetSocketAddress(sIp, sPort));
//            NettyClient nettyClient = NettyClient.getInstance();
//            nettyClient.connect();
//            Log.d(TAG, "Connected");
//        } catch (Exception e) {
//            Log.d(TAG, "Exception in service run", e);
//        }
    }

    private void sendMessage(Object message, boolean keepAlive) {
//        networkClient.sendMessage(message, keepAlive);
    }

    private void configureNetworkClientFactory(SevenAteNineClientFactory factory) {
        factory.addMessageHandler(
                m -> m.doLogic(responseHandler),
                InCommandInterface.class
        );
        factory.setCustomEncoder(o -> {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(o);
            jsonElement.getAsJsonObject().addProperty(TYPE_FIELD, o.getClass().getSimpleName());
            return gson.toJson(jsonElement);
        });
        factory.setCustomDecoder(json -> {
            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getName().equals(COMMAND_TYPE_FIELD);
                }
                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }).create();
            Log.d(TAG, "Got json: " + json);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            String type = obj.get(COMMAND_TYPE_FIELD).getAsString();
            Log.d(TAG, "Parsed type: " + type);
            Class clazz = CommandsTypeMapper.getClass(type);
            return gson.fromJson(json, clazz);
        });
        factory.setExceptionHandler(e -> {
            Log.e(TAG, "Unexpected network error", e);
        });
    }
}
