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

import com.yatty.sevenatenine.api.Card;
import com.yatty.sevenatenine.api.DisconnectRequest;
import com.yatty.sevenatenine.api.GameStartedEvent;
import com.yatty.sevenatenine.api.MoveRejectedResponse;
import com.yatty.sevenatenine.api.MoveRequest;
import com.yatty.sevenatenine.api.NewStateEvent;

import java.util.Arrays;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity {
    private static final String GAME_ID_KEY = "game_id_game_activity";
    public static final String PLAYER_NAME_KEY = "player_name_game_activity";
    public static final String PLUS_MINUS_SYMBOL = "±";
    public static final String NEW_LINE_SYMBOL = "\n";

    private static final int VIBRATE_TIME_MS = 100;
    public static final int MAX_NUM_CARDS_ON_TABLE = 10;
    public static final String TAG = "TAG";

    private NettyClient nettyClient;
    private String gameId;
    private String playerName;
    private TextView cardTextView;
    private TextView counterTextView;
    private TextView currentCardModifierTextView;
    private Button disconnectButton;
    private Button getCardButton;
    private Button cardsOnTableButtons[];
    private Vibrator vibrator;

    private Card currentCard;
    private LinkedList<Card> cardDeckLinkedList;
    private int numOfCardsOnDesk;
    private int moveNumber;

    public static Intent newIntent(Context context, String gameId, String playerName) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_ID_KEY, gameId);
        intent.putExtra(PLAYER_NAME_KEY, playerName);
        return intent;
    }

    private void initUi() {
        cardTextView = findViewById(R.id.card_tv);
        counterTextView = findViewById(R.id.counter_text_view);
        currentCardModifierTextView = findViewById(R.id.tv_current_card_modifier);
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
                DisconnectRequest disconnectRequest = new DisconnectRequest();
                nettyClient.write(disconnectRequest);
                Intent nextActivity = MainActivity.newInstance(getApplicationContext());
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
                    Card card = cardDeckLinkedList.getFirst();
                    cardDeckLinkedList.pollFirst();
                    cardsOnTableButtons[numOfCardsOnDesk].setText(card.getValue() + NEW_LINE_SYMBOL +
                            PLUS_MINUS_SYMBOL + card.getModifier());
                    cardsOnTableButtons[numOfCardsOnDesk].setOnClickListener(new CardButtonOnClickListener(card));
                    numOfCardsOnDesk++;
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
                String messageStr = (String) msg.obj;
                if (messageStr.equals(GameStartedEvent.COMMAND_TYPE)) {
                    currentCard = (Card) msg.getData().getSerializable(Constants.FIRST_CARD_KEY);
                    cardDeckLinkedList = new LinkedList(Arrays.asList((Card[]) msg.getData().
                            getSerializable(Constants.CARD_DECK_KEY)));
                    numOfCardsOnDesk = 0;
                    cardTextView.setText(String.valueOf(currentCard.getValue()) + PLUS_MINUS_SYMBOL +
                            NEW_LINE_SYMBOL + String.valueOf(currentCard.getModifier()));
                } else if (messageStr.equals(MoveRejectedResponse.COMMAND_TYPE)) {
                    vibrator.vibrate(VIBRATE_TIME_MS);
                } else if (messageStr.equals(NewStateEvent.COMMAND_TYPE)) {
                    String player = msg.getData().getString(Constants.PLAYER_WITH_RIGHT_ANSWER_KEY);
                    if (player.equals(playerName)) {
                        counterTextView.setText(String.valueOf(Integer.parseInt(counterTextView.getText().toString()) + 1));
                    }
                    int card = msg.getData().getInt(Constants.NEXT_CARD_KEY);
                    cardTextView.setText(String.valueOf(card));
                    moveNumber++;
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
            if ((currentCard.getValue() + currentCard.getModifier()) == card.getValue() ||
                    (currentCard.getValue() - currentCard.getModifier()) == card.getValue()) {
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setGameId(gameId);
                moveRequest.setMove(card);
                moveRequest.setMoveNumber(moveNumber);
                nettyClient.write(moveRequest);
            } else {
                vibrator.vibrate(VIBRATE_TIME_MS);
            }
        }
    }

    @Override
    protected void onPause() {
        DisconnectRequest disconnectRequest = new DisconnectRequest();
        nettyClient.write(disconnectRequest);
        super.onPause();
    }
}
