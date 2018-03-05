package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PlayerResult;

import java.util.Arrays;

public class GameOverActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static final String CARDS_LEFT_KEY = "cards_left_key";
    private static final String WINNER_NAME_KEY = "winner_name_key";
    private static final String PLAYER_NAME_KEY = "player_name_key";
    private TextView winnerNameTextView;
    private Button toMainMenuButton;
    private TextView gameOverText;
    private String playerName;
    private String winnerName;
    private ListView scoreBoard;
    PlayerResult[] scores;


    public static Intent newIntent(Context context, String playerName, String winnerName, PlayerResult[] scores) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        intent.putExtra(WINNER_NAME_KEY, winnerName);
        intent.putExtra(CARDS_LEFT_KEY, scores);
        return intent;
    }

    private class ScoreBoardAdapter extends ArrayAdapter<PlayerResult> {
        public ScoreBoardAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2, scores);
            //this.sort();
        }

        @Override
        public View getView(int position, View listView, ViewGroup parent) {
            PlayerResult result = getItem(position);

            if (listView == null) {
                listView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) listView.findViewById(android.R.id.text1))
                    .setText(result.getPlayerName());
            ((TextView) listView.findViewById(android.R.id.text2))
                    .setText("Cards left: " + result.getCardsLeft());
            return listView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        gameOverText = findViewById(R.id.tv_gameOver);
        winnerNameTextView = findViewById(R.id.tv_winnerName);
        toMainMenuButton = findViewById(R.id.btn_toMainMenu);
        scoreBoard = findViewById(R.id.lv_scoreBoard);
        Intent intent = getIntent();
        playerName = intent.getStringExtra(PLAYER_NAME_KEY);
        winnerName = intent.getStringExtra(WINNER_NAME_KEY);
        Parcelable parcelableArray[] = intent.getParcelableArrayExtra(CARDS_LEFT_KEY);
        if (parcelableArray != null) {
            scores = Arrays.copyOf(parcelableArray, parcelableArray.length, PlayerResult[].class);
        }
        if (scores == null) {
            /*PlayerResult pr1 = new PlayerResult("Ivan",12);
            PlayerResult pr2 = new PlayerResult("Petr",11);
            PlayerResult pr3 = new PlayerResult("Fedor",13);
            scores = new PlayerResult[3];
            scores[0] = pr1;
            scores[1] = pr2;
            scores[2] = pr3;*/
        }
        sortByCardCount(scores);
        if (winnerName == null) winnerName = scores[0].getPlayerName();
        if (playerName.equals(winnerName)) gameOverText.setText("You Win!");
        else gameOverText.setText("Better luck next time!");
        winnerNameTextView.setText(winnerName);
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

    public void sortByCardCount(PlayerResult[] res) {
        PlayerResult tmp;
        for (int i = 0; i < res.length; i++)
            for (int j = i + 1; j < res.length; j++)
                if (res[j].getCardsLeft() < res[i].getCardsLeft()) {
                    tmp = res[i];
                    res[i] = res[j];
                    res[j] = tmp;
                }
    }
}
