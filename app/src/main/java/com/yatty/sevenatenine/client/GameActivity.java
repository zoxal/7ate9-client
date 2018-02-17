package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yatty.sevenatenine.api.GameStartedEvent;
import com.yatty.sevenatenine.api.MoveRejectedResponse;
import com.yatty.sevenatenine.api.MoveRequest;
import com.yatty.sevenatenine.api.NewStateEvent;

public class GameActivity extends AppCompatActivity {
    private static final String GAME_ID_KEY = "game_id_game_activity";
    public static final String PLAYER_NAME_KEY = "player_name_game_activity";
    //private static final String BUNDLE_ID = "bundle_game_activity";
    public static final String TAG = "TAG";

    private NettyClient nettyClient;
    private String gameId;
    private String playerName;
    private TextView cardTextView;
    private TextView counterTextView;
    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;

    public static Intent newIntent(Context context, String gameId, String playerName) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_ID_KEY, gameId);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameId = getIntent().getStringExtra(GAME_ID_KEY);
        playerName = getIntent().getStringExtra(PLAYER_NAME_KEY);
        cardTextView = findViewById(R.id.card_tv);
        counterTextView = findViewById(R.id.counter_text_view);
        firstButton = findViewById(R.id.first_button);
        secondButton = findViewById(R.id.second_button);
        thirdButton = findViewById(R.id.third_button);
        ButtonsListener buttonsListener = new ButtonsListener();
        firstButton.setOnClickListener(buttonsListener);
        secondButton.setOnClickListener(buttonsListener);
        thirdButton.setOnClickListener(buttonsListener);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "GameActivity: handle");
                String messageStr = (String) msg.obj;
                if (messageStr.equals(GameStartedEvent.COMMAND_TYPE)) {
                    int card = msg.getData().getInt(Constants.NEXT_CARD_KEY);
                    cardTextView.setText(String.valueOf(card));
                } else if (messageStr.equals(MoveRejectedResponse.COMMAND_TYPE)) {
                    Toast.makeText(getApplicationContext(), "NO!", Toast.LENGTH_SHORT).show();
                } else if (messageStr.equals(NewStateEvent.COMMAND_TYPE)) {
                    String player = msg.getData().getString(Constants.PLAYER_WITH_RIGHT_ANSWER_KEY);
                    if (player.equals(playerName)) {
                        counterTextView.setText(String.valueOf(Integer.parseInt(counterTextView.getText().toString()) + 1));
                    }
                    int card = msg.getData().getInt(Constants.NEXT_CARD_KEY);
                    cardTextView.setText(String.valueOf(card));
                }
            }
        };
        nettyClient = NettyClient.getInstance(handler);
    }

    class ButtonsListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int card;
            switch (view.getId()) {
                case R.id.first_button:
                    card = 1;
                    break;
                case R.id.second_button:
                    card = 2;
                    break;
                case R.id.third_button:
                    card = 3;
                    break;
                default:
                    card = 1;
                    break;
            }
            MoveRequest moveRequest = new MoveRequest();
            moveRequest.setMove(card);
            moveRequest.setGameId(gameId);
            nettyClient.write(moveRequest);
        }
    }
}
