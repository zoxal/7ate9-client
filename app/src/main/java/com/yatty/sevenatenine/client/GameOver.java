package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {
    private static final String SCORE_KEY = "score_from_game_activity";
    public static final String ISWINNER_KEY = "is_winner";
    private TextView gameScoreTextView;
    private TextView isWinnerTextView;
    private String score;
    private Boolean isWinner;
    private Button toMainMenuButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameScoreTextView = findViewById(R.id.counter_text_view);
        isWinnerTextView = findViewById(R.id.is_winner_text_view);
        toMainMenuButton=findViewById(R.id.goToMainMenu_button);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        score = intent.getStringExtra(SCORE_KEY);
        isWinner = intent.getBooleanExtra(ISWINNER_KEY,false);
        gameScoreTextView.setText(score);
        if(isWinner)
            isWinnerTextView.setText("You Win!");
        else
            isWinnerTextView.setText("You Lose(");
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
