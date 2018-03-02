package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.yatty.sevenatenine.api.PlayerResult;

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
    private Context context;
    PlayerResult[] scores;
    private ListView scoreBoard;

    public static Intent newIntent(Context context, String playerName, String winnerName, PlayerResult[] scores) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        intent.putExtra(WINNER_NAME_KEY, winnerName);
        intent.putExtra(CARDS_LEFT_KEY, scores);
        return intent;
    }

    private class scoreBoardAdapter extends ArrayAdapter<PlayerResult>{
        public scoreBoardAdapter(Context context){
            super(context,android.R.layout.simple_list_item_2,scores);
        }

        public View getView(int position, View listView, ViewGroup parent){
            PlayerResult result = getItem(position);

            if (listView == null) {
                listView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) listView.findViewById(android.R.id.text1))
                    .setText(result.getPlayerName());
            ((TextView) listView.findViewById(android.R.id.text2))
                    .setText("Cards left: "+result.getCardsLeft());
            return listView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameScoreTextView = findViewById(R.id.counter_text_view);
        isWinnerTextView = findViewById(R.id.is_winner_text_view);
        toMainMenuButton = findViewById(R.id.goToMainMenu_button);
        scoreBoard = findViewById(R.id.scoreBoard_list_view);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        playerName = intent.getStringExtra(PLAYER_NAME_KEY);
        winnerName = intent.getStringExtra(WINNER_NAME_KEY);
        scores = (PlayerResult[]) intent.getSerializableExtra(CARDS_LEFT_KEY);
        scores[0].sortByCardCount(scores);


        Log.d(TAG, "Winner: " + winnerName);
        Log.d(TAG, "Player name: " + playerName);
        Log.d(TAG, "Scores:");
        for (int i = 0; i < scores.length; i++) {
            Log.d(TAG, scores[i].getPlayerName() + ": " + scores[i].getCardsLeft());
        }

        ArrayAdapter<PlayerResult> adapter = new scoreBoardAdapter(this);
        scoreBoard.setAdapter(adapter);

        toMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = MainActivity.newIntent(getApplicationContext());
                startActivity(mainIntent);
                finish();
            }
        });

    }


}
