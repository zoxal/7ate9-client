package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yatty.sevenatenine.api.Card;
import com.yatty.sevenatenine.api.DisconnectRequest;
import com.yatty.sevenatenine.api.GameStartedEvent;
import com.yatty.sevenatenine.api.MoveRejectedResponse;
import com.yatty.sevenatenine.api.MoveRequest;
import com.yatty.sevenatenine.api.NewStateEvent;

public class GameActivity extends AppCompatActivity {
    private static final String GAME_ID_KEY = "game_id_game_activity";
    public static final String PLAYER_NAME_KEY = "player_name_game_activity";
    public static final String PLUS_MINUS_SYMBOL = "±";
    public static final String NEW_LINE_SYMBOL = "\n";

    private static final int VIBRATE_TIME_MS = 100;
    public static final String TAG = "TAG";

    private NettyClient nettyClient;
    private String gameId;
    private String playerName;
    private TextView cardTextView;
    private TextView counterTextView;
    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button disconnectButton;
    private Vibrator vibrator;

    private Card cardOnTable;
    private Card cardDeck[];

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
        disconnectButton = findViewById(R.id.disconnect_button);
        ButtonsListener buttonsListener = new ButtonsListener();
        firstButton.setOnClickListener(buttonsListener);
        secondButton.setOnClickListener(buttonsListener);
        thirdButton.setOnClickListener(buttonsListener);
        disconnectButton.setOnClickListener(buttonsListener);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "GameActivity: handle");
                String messageStr = (String) msg.obj;
                if (messageStr.equals(GameStartedEvent.COMMAND_TYPE)) {
                    cardOnTable = (Card) msg.getData().getSerializable(Constants.FIRST_CARD_KEY);
                    cardDeck = (Card[]) msg.getData().getSerializable(Constants.CARD_DECK_KEY);
                    cardTextView.setText(String.valueOf(cardOnTable.getValue()) + PLUS_MINUS_SYMBOL +
                            NEW_LINE_SYMBOL + String.valueOf(cardOnTable.getModifier()));
                } else if (messageStr.equals(MoveRejectedResponse.COMMAND_TYPE)) {
                    vibrator.vibrate(VIBRATE_TIME_MS);
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
            if (view.getId() == R.id.disconnect_button) {
                DisconnectRequest disconnectRequest = new DisconnectRequest();
                nettyClient.write(disconnectRequest);
                Intent nextActivity = MainActivity.newInstance(getApplicationContext());
                startActivity(nextActivity);
                finish();
                return;
            }
            int card;
            switch (view.getId()) {

            }
            MoveRequest moveRequest = new MoveRequest();
            moveRequest.setGameId(gameId);
            nettyClient.write(moveRequest);
        }
    }

    @Override
    protected void onPause() {
        DisconnectRequest disconnectRequest = new DisconnectRequest();
        nettyClient.write(disconnectRequest);
        super.onPause();
    }
}
