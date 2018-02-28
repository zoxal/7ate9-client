package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {
    private static final String PLAYER_SCORE = "score_from_game_activity";
    public static final String WINNER_NAME = "player2";
    public static final String PLAYER_NAME = "player";
    private TextView gameScoreTextView;
    private TextView isWinnerTextView;
    private String score;
    private String winnerName;
    private String playerName;
    private Button toMainMenuButton;

    public static Intent newIntent(Context context, int score, String winnerName, String playerName) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(PLAYER_SCORE, score);
        intent.putExtra(WINNER_NAME, winnerName);
        intent.putExtra(PLAYER_NAME, playerName);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameScoreTextView = findViewById(R.id.counter_text_view);
        isWinnerTextView = findViewById(R.id.is_winner_text_view);
        toMainMenuButton=findViewById(R.id.goToMainMenu_button);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        score = intent.getStringExtra(PLAYER_SCORE);
        winnerName = intent.getStringExtra(WINNER_NAME);
        playerName = intent.getStringExtra(PLAYER_NAME);
        gameScoreTextView.setText(score);
        if(winnerName.equals(playerName))
            isWinnerTextView.setText("You Win!");
        else
            isWinnerTextView.setText("Winner is "+winnerName);
        toMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent=new Intent(GameOver.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }


}
