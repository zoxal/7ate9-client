package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    private NettyClient nettyClient;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, GameActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "Handler get message: " + msg.what);
            }
        };
        nettyClient = NettyClient.getInstance(handler);
    }
}
