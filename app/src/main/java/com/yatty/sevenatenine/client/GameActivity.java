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
import android.widget.TableRow;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.Card;
import com.yatty.sevenatenine.api.in_commands.GameStartedEvent;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateEvent;
import com.yatty.sevenatenine.api.out_commands.DisconnectRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private static final String GAME_ID_KEY = "game_id_game_activity";
    public static final String PLAYER_NAME_KEY = "player_name_game_activity";
    public static final String PLUS_MINUS_SYMBOL = "±";
    public static final String NEW_LINE_SYMBOL = "\n";

    private static final int VIBRATE_TIME_MS = 100;
    public static final int MAX_NUM_CARDS_ON_TABLE = 10;
    public static final int MAX_CARD = 10;
    public static final String TAG = "TAG";

    private NettyClient nettyClient;
    private String gameId;
    private String playerName;
    private TextView topCardValueTextView;
    private TextView counterTextView;
    private TextView topCardModifierTextView;
    private Button disconnectButton;
    private Button getCardButton;
    private Button cardsOnTableButtons[];
    private Vibrator vibrator;

    private Card topCard;
    private ArrayList<Card> cardDeckArrayList;
    private int numOfCardsOnDesk;
    private int moveNumber;

    public static Intent newIntent(Context context, String gameId, String playerName) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_ID_KEY, gameId);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        return intent;
    }

    private void initUi() {
        topCardValueTextView = findViewById(R.id.tv_top_card_value);
        topCardModifierTextView = findViewById(R.id.tv_top_card_modifier);
        counterTextView = findViewById(R.id.counter_text_view);
        disconnectButton = findViewById(R.id.disconnect_button);
        TableRow firstCardRow = findViewById(R.id.tr_first_card_row);
        TableRow secondCardRow = findViewById(R.id.tr_second_card_row);
        cardsOnTableButtons = new Button[MAX_NUM_CARDS_ON_TABLE];
        for (int i = 0; i < firstCardRow.getVirtualChildCount(); i++) {
            cardsOnTableButtons[i] = (Button) firstCardRow.getVirtualChildAt(i);
            cardsOnTableButtons[i].setVisibility(View.INVISIBLE);
            cardsOnTableButtons[i + firstCardRow.getVirtualChildCount()] =
                    (Button) secondCardRow.getVirtualChildAt(i);
            cardsOnTableButtons[i + firstCardRow.getVirtualChildCount()].setVisibility(View.INVISIBLE);
        }

        disconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DisconnectRequest disconnectRequest = new DisconnectRequest(gameId);
                nettyClient.write(disconnectRequest);
                Intent nextActivity = MainActivity.newIntent(getApplicationContext());
                startActivity(nextActivity);
                finish();
            }
        });
        getCardButton = findViewById(R.id.button_get_card);
        getCardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (numOfCardsOnDesk < MAX_NUM_CARDS_ON_TABLE) {
                    cardsOnTableButtons[numOfCardsOnDesk].setVisibility(View.VISIBLE);
                    if (!cardDeckArrayList.isEmpty()) {
                        Card card = cardDeckArrayList.get(0);
                        cardDeckArrayList.remove(0);
                        int i = 0;
                        while (cardsOnTableButtons[i].hasOnClickListeners()) {
                            i++;
                        }
                        cardsOnTableButtons[i].setText(card.getValue() + NEW_LINE_SYMBOL +
                                PLUS_MINUS_SYMBOL + card.getModifier());
                        cardsOnTableButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                        cardsOnTableButtons[i].setVisibility(View.VISIBLE);
                        numOfCardsOnDesk++;
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameId = getIntent().getStringExtra(GAME_ID_KEY);
        playerName = getIntent().getStringExtra(PLAYER_NAME_KEY);
        initUi();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "GameActivity: handle");
                if (msg.sendingUid == GameStartedEvent.UID) {
                    GameStartedEvent gameStartedEvent = (GameStartedEvent) msg.obj;
                    topCard = gameStartedEvent.getFirstCard();
                    Log.d(TAG, "GameStartedEvent: get topCard");
                    cardDeckArrayList = gameStartedEvent.getPlayerCards();
                    Log.d(TAG, "GameStartedEvent: get cardDeckArrayList");
                    numOfCardsOnDesk = 0;
                    topCardValueTextView.setText(String.valueOf(topCard.getValue()));
                    topCardModifierTextView.setText(PLUS_MINUS_SYMBOL + topCard.getModifier());
                } else if (msg.sendingUid == MoveRejectedResponse.UID) {
                    MoveRejectedResponse moveRejectedResponse = (MoveRejectedResponse) msg.obj;
                    Card card = moveRejectedResponse.getMove();
                    int i = 0;
                    while (cardsOnTableButtons[i].hasOnClickListeners()) {
                        i++;
                    }
                    cardsOnTableButtons[i].setText(card.getValue() + NEW_LINE_SYMBOL +
                            PLUS_MINUS_SYMBOL + card.getModifier());
                    cardsOnTableButtons[i].setOnClickListener(new CardButtonOnClickListener(card));
                    cardsOnTableButtons[i].setVisibility(View.VISIBLE);
                    numOfCardsOnDesk++;
                    vibrator.vibrate(VIBRATE_TIME_MS);
                } else if (msg.sendingUid == NewStateEvent.UID) {
                    NewStateEvent newStateEvent = (NewStateEvent) msg.obj;
                    if (newStateEvent.isLastMove()) {
                        // Test this method call!!!
                        nettyClient.setHandler(null);
                        Intent nextActivity = GameOverActivity.newIntent(getApplicationContext(), playerName,
                                newStateEvent.getGameResult().getWinner(), newStateEvent.getGameResult().getScores());
                        startActivity(nextActivity);
                        finish();
                    } else {
                        String moveWinner = newStateEvent.getMoveWinner();
                        if (playerName.equals(moveWinner)) {
                            counterTextView.setText(String.valueOf(Integer.parseInt(counterTextView.getText().toString()) + 1));
                        }
                        topCard = newStateEvent.getNextCard();
                        moveNumber = newStateEvent.getMoveNumber();
                        topCardValueTextView.setText(String.valueOf(topCard.getValue()));
                        topCardModifierTextView.setText(PLUS_MINUS_SYMBOL + topCard.getModifier());
                    }
                }
            }
        };
        nettyClient = NettyClient.getInstance(handler);
    }

    class CardButtonOnClickListener implements View.OnClickListener {
        private Card card;

        CardButtonOnClickListener(Card card) {
            this.card = card;
        }

        @Override
        public void onClick(View view) {
            int rightValue1 = topCard.getValue() + topCard.getModifier();
            if (rightValue1 > MAX_CARD) {
                rightValue1 -= MAX_CARD;
            }
            int rightValue2 = topCard.getValue() - topCard.getModifier();
            if (rightValue2 <= 0) {
                rightValue2 += MAX_CARD;
            }
            if (card.getValue() == rightValue1 ||
                    card.getValue() == rightValue2) {
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setGameId(gameId);
                moveRequest.setMove(card);
                moveRequest.setMoveNumber(moveNumber);
                nettyClient.write(moveRequest);
                view.setVisibility(View.INVISIBLE);
                view.setOnClickListener(null);
                numOfCardsOnDesk--;
            } else {
                vibrator.vibrate(VIBRATE_TIME_MS);
            }
        }
    }

    @Override
    protected void onPause() {
        /*DisconnectRequest disconnectRequest = new DisconnectRequest(gameId);
        nettyClient.write(disconnectRequest);
        */
        super.onPause();
    }
}
