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
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.Card;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateNotification;
import com.yatty.sevenatenine.api.out_commands.LogOutRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = GameActivity.class.getSimpleName();
    private static final String EXTRA_GAME_STARTED_EVENT = "game_started_event";
    public static final String PLUS_MINUS_SYMBOL = "±";
    public static final String NEW_LINE_SYMBOL = "\n";

    private static final int VIBRATE_TIME_MS = 100;
    public static final int MAX_NUM_CARDS_ON_TABLE = 10;
    public static final int MAX_CARD = 10;

    private NettyClient mNettyClient;
    private String mGameId;
    private TextView mTopCardValueTextView;
    private TextView mCounterTextView;
    private TextView mTopCardModifierTextView;
    private Button mDisconnectButton;
    private ImageButton mGetCardImageButton;
    private ImageButton mCardsOnTableImageButtons[];
    private Vibrator mVibrator;

    private Card mTopCard;
    private ArrayList<Card> mCardArrayList;
    private int mNumOfCardsOnDesk;
    private int mMoveNumber;

    private void initUi() {
        mTopCardValueTextView = findViewById(R.id.tv_top_card_value);
        mTopCardModifierTextView = findViewById(R.id.tv_top_card_modifier);
        mCounterTextView = findViewById(R.id.tv_counter);
        mDisconnectButton = findViewById(R.id.button_disconnect);
        TableRow firstCardRow = findViewById(R.id.tr_first_card_row);
        TableRow secondCardRow = findViewById(R.id.tr_second_card_row);
        mCardsOnTableImageButtons = new ImageButton[MAX_NUM_CARDS_ON_TABLE];
        for (int i = 0; i < firstCardRow.getVirtualChildCount(); i++) {
            mCardsOnTableImageButtons[i] = (ImageButton) firstCardRow.getVirtualChildAt(i);
            mCardsOnTableImageButtons[i].setVisibility(View.INVISIBLE);
            mCardsOnTableImageButtons[i + firstCardRow.getVirtualChildCount()] =
                    (ImageButton) secondCardRow.getVirtualChildAt(i);
            mCardsOnTableImageButtons[i + firstCardRow.getVirtualChildCount()].setVisibility(View.INVISIBLE);
        }

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LogOutRequest logOutRequest = new LogOutRequest(UserInfo.getAuthToken());
                mNettyClient.write(logOutRequest, false);
                mNettyClient.setHandler(null);
                Intent nextActivity = MainActivity.newIntent(getApplicationContext());
                startActivity(nextActivity);
                finish();
            }
        });
        mGetCardImageButton = findViewById(R.id.button_get_card);
        mGetCardImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mNumOfCardsOnDesk < MAX_NUM_CARDS_ON_TABLE) {
                    if (!mCardArrayList.isEmpty()) {
                        Card card = mCardArrayList.get(0);
                        mCardArrayList.remove(0);
                        int i = 0;
                        while (mCardsOnTableImageButtons[i].hasOnClickListeners()) {
                            i++;
                        }
                        mCardsOnTableImageButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                        mCardsOnTableImageButtons[i].setVisibility(View.VISIBLE);
                        mNumOfCardsOnDesk++;

                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GameStartedNotification gameStartedNotification = getIntent().getParcelableExtra(EXTRA_GAME_STARTED_EVENT);
        initUi();
        mTopCard = gameStartedNotification.getFirstCard();
        Log.d(TAG, "GameStartedNotification: get mTopCard");
        mCardArrayList = gameStartedNotification.getPlayerCards();
        Log.d(TAG, "GameStartedNotification: get mCardArrayList");
        mGameId = gameStartedNotification.getLobbyId();
        mNumOfCardsOnDesk = 0;
        mTopCardValueTextView.setText(String.valueOf(mTopCard.getValue()));
        mTopCardModifierTextView.setText(PLUS_MINUS_SYMBOL + mTopCard.getModifier());

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new GameActivityHandler();
        mNettyClient = NettyClient.getInstance();
        mNettyClient.setHandler(handler);
    }

    public static Intent getStartIntent(Context context, GameStartedNotification gameStartedNotification) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(EXTRA_GAME_STARTED_EVENT, gameStartedNotification);
        return intent;
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    class CardButtonOnClickListener implements View.OnClickListener {
        private Card card;

        CardButtonOnClickListener(Card card) {
            this.card = card;
        }

        @Override
        public void onClick(View view) {
            int rightValue1 = mTopCard.getValue() + mTopCard.getModifier();
            if (rightValue1 > MAX_CARD) {
                rightValue1 -= MAX_CARD;
            }
            int rightValue2 = mTopCard.getValue() - mTopCard.getModifier();
            if (rightValue2 <= 0) {
                rightValue2 += MAX_CARD;
            }
            if (card.getValue() == rightValue1 ||
                    card.getValue() == rightValue2) {
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setGameId(mGameId);
                moveRequest.setMove(card);
                moveRequest.setAuthToken(UserInfo.getAuthToken());
                moveRequest.setMoveNumber(mMoveNumber);
                mNettyClient.write(moveRequest, true);
                view.setVisibility(View.INVISIBLE);
                view.setOnClickListener(null);
                mNumOfCardsOnDesk--;
            } else {
                mVibrator.vibrate(VIBRATE_TIME_MS);
            }
        }
    }

    private class GameActivityHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(TAG, "GameActivity: handle");
            if (msg.obj instanceof MoveRejectedResponse) {
                MoveRejectedResponse moveRejectedResponse = (MoveRejectedResponse) msg.obj;
                Card card = moveRejectedResponse.getMove();
                int i = 0;
                while (mCardsOnTableImageButtons[i].hasOnClickListeners()) {
                    i++;
                }
                mCardsOnTableImageButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                mCardsOnTableImageButtons[i].setVisibility(View.VISIBLE);
                mNumOfCardsOnDesk++;
                mVibrator.vibrate(VIBRATE_TIME_MS);
            } else if (msg.obj instanceof NewStateNotification) {
                NewStateNotification newStateNotification = (NewStateNotification) msg.obj;
                if (newStateNotification.isLastMove()) {
                    mNettyClient.setHandler(null);
                    Intent nextActivity = GameOverActivity.newIntent(getApplicationContext(), UserInfo.getUserName(),
                            newStateNotification.getGameResult().getWinner(), newStateNotification.getGameResult().getScores());
                    startActivity(nextActivity);
                    finish();
                } else {
                    String moveWinner = newStateNotification.getMoveWinner();
                    if (UserInfo.getUserName().equals(moveWinner)) {
                        mCounterTextView.setText(String.valueOf(Integer.parseInt(mCounterTextView.getText().toString()) + 1));
                    }
                    mTopCard = newStateNotification.getNextCard();
                    mMoveNumber = newStateNotification.getMoveNumber();
                    mTopCardValueTextView.setText(String.valueOf(mTopCard.getValue()));
                    mTopCardModifierTextView.setText(PLUS_MINUS_SYMBOL + mTopCard.getModifier());
                }
            }
        }
    }
}
