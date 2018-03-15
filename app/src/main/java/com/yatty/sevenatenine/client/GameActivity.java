package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.Card;
import com.yatty.sevenatenine.api.in_commands.GameStartedEvent;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateEvent;
import com.yatty.sevenatenine.api.out_commands.LogOutRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = GameActivity.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "game_id_game_activity";
    //    public static final String EXTRA_PLAYER_NAME = "player_name_game_activity";
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
    private Button mGetCardButton;
    private Button mCardsOnTableButtons[];
    private Vibrator mVibrator;
    private ProgressBar mProgressBar;

    private Card mTopCard;
    private ArrayList<Card> mCardArrayList;
    private int mNumOfCardsOnDesk;
    private int mMoveNumber;

    public static Intent newIntent(Context context, String gameId) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        return intent;
    }

    private void initUi() {
        mTopCardValueTextView = findViewById(R.id.tv_top_card_value);
        mTopCardModifierTextView = findViewById(R.id.tv_top_card_modifier);
        mCounterTextView = findViewById(R.id.tv_counter);
        mDisconnectButton = findViewById(R.id.button_disconnect);
        TableRow firstCardRow = findViewById(R.id.tr_first_card_row);
        TableRow secondCardRow = findViewById(R.id.tr_second_card_row);
        mCardsOnTableButtons = new Button[MAX_NUM_CARDS_ON_TABLE];
        for (int i = 0; i < firstCardRow.getVirtualChildCount(); i++) {
            mCardsOnTableButtons[i] = (Button) firstCardRow.getVirtualChildAt(i);
            mCardsOnTableButtons[i].setVisibility(View.INVISIBLE);
            mCardsOnTableButtons[i + firstCardRow.getVirtualChildCount()] =
                    (Button) secondCardRow.getVirtualChildAt(i);
            mCardsOnTableButtons[i + firstCardRow.getVirtualChildCount()].setVisibility(View.INVISIBLE);
        }

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LogOutRequest logOutRequest = new LogOutRequest(mGameId);
                mNettyClient.write(logOutRequest, false);
                mNettyClient.setHandler(null);
                Intent nextActivity = MainActivity.newIntent(getApplicationContext());
                startActivity(nextActivity);
                finish();
            }
        });
        mGetCardButton = findViewById(R.id.button_get_card);
        mGetCardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mNumOfCardsOnDesk < MAX_NUM_CARDS_ON_TABLE) {
                    if (!mCardArrayList.isEmpty()) {
                        Card card = mCardArrayList.get(0);
                        mCardArrayList.remove(0);
                        int i = 0;
                        while (mCardsOnTableButtons[i].hasOnClickListeners()) {
                            i++;
                        }
                        mCardsOnTableButtons[i].setText(card.getValue() + NEW_LINE_SYMBOL +
                                PLUS_MINUS_SYMBOL + card.getModifier());
                        mCardsOnTableButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                        mCardsOnTableButtons[i].setVisibility(View.VISIBLE);
                        mNumOfCardsOnDesk++;
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mGameId = getIntent().getStringExtra(EXTRA_GAME_ID);
//        mPlayerName = getIntent().getStringExtra(EXTRA_PLAYER_NAME);
        initUi();

        mProgressBar = new ProgressBar(getApplicationContext());
        FrameLayout frameLayout = findViewById(R.id.fl_main_layout);
        frameLayout.addView(mProgressBar, FrameLayout.LayoutParams.MATCH_PARENT);
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new GameActivityHandler();
        mNettyClient = NettyClient.getInstance();
        mNettyClient.setHandler(handler);
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

    @Override
    protected void onPause() {
        /*LogOutRequest disconnectRequest = new LogOutRequest(mGameId);
        mNettyClient.write(disconnectRequest);
        */
        super.onPause();
    }

    private class GameActivityHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(TAG, "GameActivity: handle");
            if (msg.obj instanceof GameStartedEvent) {
                GameStartedEvent gameStartedEvent = (GameStartedEvent) msg.obj;
                mTopCard = gameStartedEvent.getFirstCard();
                Log.d(TAG, "GameStartedEvent: get mTopCard");
                mCardArrayList = gameStartedEvent.getPlayerCards();
                Log.d(TAG, "GameStartedEvent: get mCardArrayList");
                mNumOfCardsOnDesk = 0;
                mTopCardValueTextView.setText(String.valueOf(mTopCard.getValue()));
                mTopCardModifierTextView.setText(PLUS_MINUS_SYMBOL + mTopCard.getModifier());
                mProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (msg.obj instanceof MoveRejectedResponse) {
                MoveRejectedResponse moveRejectedResponse = (MoveRejectedResponse) msg.obj;
                Card card = moveRejectedResponse.getMove();
                int i = 0;
                while (mCardsOnTableButtons[i].hasOnClickListeners()) {
                    i++;
                }
                mCardsOnTableButtons[i].setText(card.getValue() + NEW_LINE_SYMBOL +
                        PLUS_MINUS_SYMBOL + card.getModifier());
                mCardsOnTableButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                mCardsOnTableButtons[i].setVisibility(View.VISIBLE);
                mNumOfCardsOnDesk++;
                mVibrator.vibrate(VIBRATE_TIME_MS);
            } else if (msg.obj instanceof NewStateEvent) {
                NewStateEvent newStateEvent = (NewStateEvent) msg.obj;
                if (newStateEvent.isLastMove()) {
                    mNettyClient.setHandler(null);
                    Intent nextActivity = GameOverActivity.newIntent(getApplicationContext(), UserInfo.getUserName(),
                            newStateEvent.getGameResult().getWinner(), newStateEvent.getGameResult().getScores());
                    startActivity(nextActivity);
                    finish();
                } else {
                    String moveWinner = newStateEvent.getMoveWinner();
                    if (UserInfo.getUserName().equals(moveWinner)) {
                        mCounterTextView.setText(String.valueOf(Integer.parseInt(mCounterTextView.getText().toString()) + 1));
                    }
                    mTopCard = newStateEvent.getNextCard();
                    mMoveNumber = newStateEvent.getMoveNumber();
                    mTopCardValueTextView.setText(String.valueOf(mTopCard.getValue()));
                    mTopCardModifierTextView.setText(PLUS_MINUS_SYMBOL + mTopCard.getModifier());
                }
            }
        }
    }
}
