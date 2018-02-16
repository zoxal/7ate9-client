package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.yatty.sevenatenine.api.GameStartedEvent;

public class GameActivity extends AppCompatActivity {
    private static final String GAME_ID_KEY = "game_id_game_activity";
    //private static final String BUNDLE_ID = "bundle_game_activity";
    public static final String TAG = "TAG";

    private NettyClient nettyClient;
    private String gameId;
    private TextView cardTextView;

    public static Intent newIntent(Context context, String gameId) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_ID_KEY, gameId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameId = getIntent().getStringExtra(GAME_ID_KEY);
        cardTextView = findViewById(R.id.card_tv);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "GameActivity: handle");
                String messageStr = (String) msg.obj;
                if (messageStr.equals(GameStartedEvent.COMMAND_TYPE)) {
                    int card = msg.getData().getInt(Constants.NEXT_CARD_KEY);
                    cardTextView.setText(String.valueOf(card));
                }
            }
        };
        nettyClient = NettyClient.getInstance(handler);
    }
}
