package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PlayerResult;

public class GameOverActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static final String CARDS_LEFT_KEY = "cards_left_key";
    private static final String WINNER_NAME_KEY = "winner_name_key";
    private static final String PLAYER_NAME_KEY = "player_name_key";
    private TextView gameScoreTextView;
    private TextView isWinnerTextView;
    private Button toMainMenuButton;
    private String playerName;
    private String winnerName;
    PlayerResult[] scores;


    public static Intent newIntent(Context context, String playerName, String winnerName, PlayerResult[] scores) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        intent.putExtra(WINNER_NAME_KEY, winnerName);
        intent.putExtra(CARDS_LEFT_KEY, scores);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameScoreTextView = findViewById(R.id.counter_text_view);
        isWinnerTextView = findViewById(R.id.is_winner_text_view);
        toMainMenuButton = findViewById(R.id.goToMainMenu_button);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        playerName = intent.getStringExtra(PLAYER_NAME_KEY);
        winnerName = intent.getStringExtra(WINNER_NAME_KEY);
        scores = (PlayerResult[]) intent.getSerializableExtra(CARDS_LEFT_KEY);

        Log.d(TAG, "Winner: " + winnerName);
        Log.d(TAG, "Player name: " + playerName);
        Log.d(TAG, "Scores:");
        for (int i = 0; i < scores.length; i++) {
            Log.d(TAG, scores[i].getPlayerName() + ": " + scores[i].getCardsLeft());
        }
        /*
        if (winnerName.equals(playerName))
            isWinnerTextView.setText("You Win!");
        else
            isWinnerTextView.setText("Winner is " + winnerName);
        toMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = MainActivity.newIntent(getApplicationContext());
                startActivity(mainIntent);
                finish();
            }
        });
        */
    }


}
