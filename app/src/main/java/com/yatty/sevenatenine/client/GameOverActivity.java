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
    public static final String TAG = GameOverActivity.class.getSimpleName();
    private static final String EXTRA_CARDS_LEFT = "cards_left_key";
    private static final String EXTRA_WINNER_NAME = "winner_name_key";
    private static final String EXTRA_PLAYER_NAME = "player_name_key";
    private TextView mWinnerNameTextView;
    private Button mToMainMenuButton;
    private TextView mGameOverText;
    private String mPlayerName;
    private String mWinnerName;
    private ListView mScoreBoard;
    PlayerResult[] mScores;


    public static Intent newIntent(Context context, String playerName, String winnerName, PlayerResult[] scores) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(EXTRA_PLAYER_NAME, playerName);
        intent.putExtra(EXTRA_WINNER_NAME, winnerName);
        intent.putExtra(EXTRA_CARDS_LEFT, scores);
        return intent;
    }

    private class ScoreBoardAdapter extends ArrayAdapter<PlayerResult> {
        public ScoreBoardAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2, mScores);
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
        mGameOverText = findViewById(R.id.tv_game_over);
        mWinnerNameTextView = findViewById(R.id.tv_winner_name);
        mToMainMenuButton = findViewById(R.id.button_to_main_menu);
        mScoreBoard = findViewById(R.id.lv_score_board);
        Intent intent = getIntent();
        mPlayerName = intent.getStringExtra(EXTRA_PLAYER_NAME);
        mWinnerName = intent.getStringExtra(EXTRA_WINNER_NAME);
        Parcelable parcelableArray[] = intent.getParcelableArrayExtra(EXTRA_CARDS_LEFT);
        if (parcelableArray != null) {
            mScores = Arrays.copyOf(parcelableArray, parcelableArray.length, PlayerResult[].class);
        }
        if (mScores == null) {
            /*PlayerResult pr1 = new PlayerResult("Ivan",12);
            PlayerResult pr2 = new PlayerResult("Petr",11);
            PlayerResult pr3 = new PlayerResult("Fedor",13);
            mScores = new PlayerResult[3];
            mScores[0] = pr1;
            mScores[1] = pr2;
            mScores[2] = pr3;*/
        }
        sortByCardCount(mScores);
        if (mWinnerName == null) mWinnerName = mScores[0].getPlayerName();
        if (mPlayerName.equals(mWinnerName)) mGameOverText.setText("You Win!");
        else mGameOverText.setText("Better luck next time!");
        mWinnerNameTextView.setText(mWinnerName);
        Log.d(TAG, "Winner: " + mWinnerName);
        Log.d(TAG, "Player name: " + mPlayerName);
        Log.d(TAG, "Scores:");
        for (int i = 0; i < mScores.length; i++) {
            Log.d(TAG, mScores[i].getPlayerName() + ": " + mScores[i].getCardsLeft());
        }
        ScoreBoardAdapter adapter = new ScoreBoardAdapter(this);
        mScoreBoard.setAdapter(adapter);
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
