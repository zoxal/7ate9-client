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

import com.yatty.sevenatenine.api.PlayerResult;

public class GameOverActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static final String CARDS_LEFT_KEY = "cards_left_key";
    private static final String WINNER_NAME_KEY = "winner_name_key";
    private static final String PLAYER_NAME_KEY = "player_name_key";
    private TextView gameScoreTextView;
    private Button toMainMenuButton;
    private String playerName;
    private String winnerName;
    PlayerResult[] scores;
    private ListView scoreBoard;

    public static Intent newIntent(Context context, String playerName, String winnerName, PlayerResult[] scores) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        intent.putExtra(WINNER_NAME_KEY, winnerName);
        intent.putExtra(CARDS_LEFT_KEY, scores);
        return intent;
    }

    private class ScoreBoardAdapter extends ArrayAdapter<PlayerResult>{
        public ScoreBoardAdapter(Context context){
            super(context,android.R.layout.simple_list_item_2,scores);
        }
        @Override
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
        setContentView(R.layout.activity_game_over);
        gameScoreTextView =findViewById(R.id.counter_text_view);
        toMainMenuButton = new Button(this);
        toMainMenuButton = findViewById(R.id.goToMainMenu_button);
        scoreBoard = new ListView(this);
        scoreBoard = findViewById(R.id.list);

        Intent intent = getIntent();
        playerName = intent.getStringExtra(PLAYER_NAME_KEY);
        winnerName = intent.getStringExtra(WINNER_NAME_KEY);
        scores = (PlayerResult[]) intent.getSerializableExtra(CARDS_LEFT_KEY);
        if(scores==null)
        {
            PlayerResult pr1 = new PlayerResult();
            scores = new PlayerResult[1];
            scores[0] = pr1;
        }
        //scores[0].sortByCardCount(scores);
        Log.d(TAG, "Winner: " + winnerName);
        Log.d(TAG, "Player name: " + playerName);
        Log.d(TAG, "Scores:");
        for (int i = 0; i < scores.length; i++) {
            Log.d(TAG, scores[i].getPlayerName() + ": " + scores[i].getCardsLeft());
        }
        ScoreBoardAdapter adapter = new ScoreBoardAdapter(this);
        scoreBoard.setAdapter(adapter);
    }

    public void clickToMainMenu_button(View view) {
        Intent mainIntent = MainActivity.newIntent(getApplicationContext());
        startActivity(mainIntent);
        finish();

    }
}
