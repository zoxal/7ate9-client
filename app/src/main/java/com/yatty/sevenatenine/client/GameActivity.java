package com.yatty.sevenatenine.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.yatty.sevenatenine.api.commands_with_data.Card;
import com.yatty.sevenatenine.api.commands_with_data.PlayerInfo;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateNotification;
import com.yatty.sevenatenine.api.out_commands.LeaveGameRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;
import com.yatty.sevenatenine.client.network.NetworkService;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = GameActivity.class.getSimpleName();
    private static final String EXTRA_GAME_STARTED_EVENT = "game_started_event";

    private static final int VIBRATE_TIME_MS = 100;
    private static final int LONG_VIBRATE_TIME_MS = 400;
    private static final int STREAMS_NUM = 3;
    private static boolean notifyMistake = false;
    public static final int MAX_NUM_CARDS_ON_TABLE = 8;
    public static final int MAX_CARD = 10;
    public static final int CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS = 300;
    public static final int GET_CARD_ANIMATION_DURATION_MILLIS = 150;

    private String mGameId;
    private ImageButton mUserDeckImageButton;
    private ImageButton mSecondCardInUserDeckImageButton;
    private ImageButton mCardsOnTableImageButtons[];
    private ImageButton mTopCardImageButton;
    private ImageButton mCardUnderTopCardImageButton;
    private ImageButton mFirstPlayerDeck;
    private ImageButton mSecondPlayerDeck;
    private ImageButton mThirdPlayerDeck;
    private TextView mFirstPlayerCardsNumTextView;
    private TextView mSecondPlayerCardsNumTextView;
    private TextView mThirdPlayerCardsNumTextView;
    private TextView mUserCardsNumTextView;
    private TextView mFirstPlayerNameTextView;
    private TextView mSecondPlayerNameTextView;
    private TextView mThirdPlayerNameTextView;
    private Vibrator mVibrator;

    private Card mTopCard;
    private ArrayList<Card> mCardArrayList;
    private int mNumOfCardsOnDesk;
    private int mMoveNumber;
    private int mSoundCardSlideId;
    private int mSoundCardSlideOpponentId;
    private int mSoundCardPlaceId;
    private SoundPool mSoundPool;

    private void initUi(GameStartedNotification gameStartedNotification) {
        mTopCardImageButton = findViewById(R.id.ib_top_card);
        mUserCardsNumTextView = findViewById(R.id.tv_user_cards_num);
        mCardUnderTopCardImageButton = findViewById(R.id.ib_card_under_top_card);
        mTopCardImageButton.bringToFront();

        mFirstPlayerDeck = findViewById(R.id.ib_first_player_deck);
        mSecondPlayerDeck = findViewById(R.id.ib_second_player_deck);
        mThirdPlayerDeck = findViewById(R.id.ib_third_player_deck);

        mFirstPlayerCardsNumTextView = findViewById(R.id.tv_first_player_cards_num);
        mSecondPlayerCardsNumTextView = findViewById(R.id.tv_second_player_cards_num);
        mThirdPlayerCardsNumTextView = findViewById(R.id.tv_third_player_cards_num);

        mFirstPlayerNameTextView = findViewById(R.id.tv_first_player_name);
        mSecondPlayerNameTextView = findViewById(R.id.tv_second_palyer_name);
        mThirdPlayerNameTextView = findViewById(R.id.tv_third_player_name);

        PlayerInfo allPlayersInfo[] = SessionInfo.getPrivateLobbyInfo().getPlayers();
        PlayerInfo otherPlayersInfo[] = new PlayerInfo[allPlayersInfo.length - 1];

        for (int i = 0, j = 0; i < allPlayersInfo.length; i++) {
            if (!allPlayersInfo[i].getPlayerId().equals(SessionInfo.getUserName())) {
                otherPlayersInfo[j] = allPlayersInfo[i];
                j++;
            }
        }
        int cardsNum = gameStartedNotification.getPlayerCards().size();
        switch (otherPlayersInfo.length) {
            case 3:
                mThirdPlayerDeck.setVisibility(View.VISIBLE);
                mThirdPlayerNameTextView.setText(otherPlayersInfo[2].getPlayerId());
                mThirdPlayerCardsNumTextView.setText(String.valueOf(cardsNum));
            case 2:
                mSecondPlayerDeck.setVisibility(View.VISIBLE);
                mSecondPlayerNameTextView.setText(otherPlayersInfo[1].getPlayerId());
                mSecondPlayerCardsNumTextView.setText(String.valueOf(cardsNum));
            case 1:
                mFirstPlayerDeck.setVisibility(View.VISIBLE);
                mFirstPlayerNameTextView.setText(otherPlayersInfo[0].getPlayerId());
                mFirstPlayerCardsNumTextView.setText(String.valueOf(cardsNum));
        }
        mUserCardsNumTextView.setText(String.valueOf(cardsNum));
        final TableRow firstCardRow = findViewById(R.id.tr_first_card_row);
        final TableRow secondCardRow = findViewById(R.id.tr_second_card_row);
        mCardsOnTableImageButtons = new ImageButton[MAX_NUM_CARDS_ON_TABLE];
        for (int i = 0; i < firstCardRow.getVirtualChildCount(); i++) {
            mCardsOnTableImageButtons[i] = (ImageButton) firstCardRow.getVirtualChildAt(i);
            mCardsOnTableImageButtons[i].setVisibility(View.INVISIBLE);
            mCardsOnTableImageButtons[i + firstCardRow.getVirtualChildCount()] =
                    (ImageButton) secondCardRow.getVirtualChildAt(i);
            mCardsOnTableImageButtons[i + firstCardRow.getVirtualChildCount()].setVisibility(View.INVISIBLE);
        }

        mUserDeckImageButton = findViewById(R.id.ib_user_deck);
        mSecondCardInUserDeckImageButton = findViewById(R.id.ib_second_card_in_user_deck);
        mUserDeckImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (BruteForceGuard.isImprisoned()) return;

                if (mNumOfCardsOnDesk < MAX_NUM_CARDS_ON_TABLE) {
                    if (!mCardArrayList.isEmpty()) {
                        final Card card = mCardArrayList.get(0);
                        mCardArrayList.remove(0);
                        if (mCardArrayList.isEmpty()) {
                            mSecondCardInUserDeckImageButton.setVisibility(View.INVISIBLE);
                        }
                        int i = 0;
                        while (mCardsOnTableImageButtons[i].hasOnClickListeners()) {
                            i++;
                        }
                        final ImageButton cardOnTableImageButton = mCardsOnTableImageButtons[i];
                        int userDeckCoordinates[] = new int[2];
                        mUserDeckImageButton.getLocationOnScreen(userDeckCoordinates);
                        int cardCoordinates[] = new int[2];
                        cardOnTableImageButton.getLocationOnScreen(cardCoordinates);
                        TranslateAnimation animation = new TranslateAnimation(
                                TranslateAnimation.RELATIVE_TO_SELF,
                                0,
                                TranslateAnimation.ABSOLUTE,
                                cardCoordinates[0] - userDeckCoordinates[0],
                                TranslateAnimation.RELATIVE_TO_SELF,
                                0,
                                TranslateAnimation.ABSOLUTE,
                                cardCoordinates[1] - userDeckCoordinates[1]
                        );
                        animation.setDuration(GET_CARD_ANIMATION_DURATION_MILLIS);
                        final Drawable cardDrawable = getDrawableCard(card);
                        final Drawable shirtDrawable = ContextCompat
                                .getDrawable(getApplicationContext(), R.drawable.shirt);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                mUserDeckImageButton.setClickable(false);
                                mUserDeckImageButton.setImageDrawable(cardDrawable);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                TableLayout userCardsTableLayout = findViewById(R.id.tl_user_cards);
                                userCardsTableLayout.bringToFront();
                                cardOnTableImageButton.setImageDrawable(cardDrawable);
                                cardOnTableImageButton.setOnClickListener(new CardButtonOnClickListener(card));
                                cardOnTableImageButton.setVisibility(View.VISIBLE);
                                mUserDeckImageButton.setClickable(true);
                                mUserDeckImageButton.setImageDrawable(shirtDrawable);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        mUserDeckImageButton.startAnimation(animation);
                        mUserDeckImageButton.bringToFront();
                        playSound(mSoundCardSlideId);
                        mNumOfCardsOnDesk++;
                        mUserCardsNumTextView.setText(String.valueOf(Integer.parseInt(
                                mUserCardsNumTextView.getText().toString()) - 1));
                    }
                }
            }
        });

    }

    private void prepareSoundPool() {
        mSoundPool = new SoundPool(STREAMS_NUM, AudioManager.STREAM_MUSIC, 0);
        mSoundCardSlideId = mSoundPool.load(this, R.raw.sound_card_slide, 1);
        mSoundCardSlideOpponentId = mSoundPool.load(this, R.raw.sound_card_slide_opponent, 1);
        mSoundCardPlaceId = mSoundPool.load(this, R.raw.sound_card_place, 1);
    }

    private void playSound(int soundId) {
        if (ApplicationSettings.areGameSoundsEnabled(this)) {
            mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        prepareSoundPool();
        ConstraintLayout parentLayout = findViewById(R.id.cl_main_layout);
        parentLayout.setBackground(ApplicationSettings.getBackgroundPicture(this));
        GameStartedNotification gameStartedNotification = getIntent().getParcelableExtra(EXTRA_GAME_STARTED_EVENT);
        initUi(gameStartedNotification);
        mTopCard = gameStartedNotification.getFirstCard();
        Log.d(TAG, "GameStartedNotification: get mTopCard");
        mCardArrayList = gameStartedNotification.getPlayerCards();
        Log.d(TAG, "GameStartedNotification: get mCardArrayList");
        mGameId = gameStartedNotification.getLobbyId();
        mNumOfCardsOnDesk = 0;
        mTopCardImageButton.setImageDrawable(getDrawableCard(mTopCard));
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new GameActivityHandler();
        NetworkService.setHandler(handler);
    }

    public static Intent getStartIntent(Context context, GameStartedNotification gameStartedNotification) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(EXTRA_GAME_STARTED_EVENT, gameStartedNotification);
        return intent;
    }

    private Drawable getDrawableCard(Card card) {
        if (card.getModifier() == 1) {
            switch (card.getValue()) {
                case 1:
                    return getResources().getDrawable(R.drawable.g1);
                case 2:
                    return getResources().getDrawable(R.drawable.g2);
                case 3:
                    return getResources().getDrawable(R.drawable.g3);
                case 4:
                    return getResources().getDrawable(R.drawable.g4);
                case 5:
                    return getResources().getDrawable(R.drawable.g5);
                case 6:
                    return getResources().getDrawable(R.drawable.g6);
                case 7:
                    return getResources().getDrawable(R.drawable.g7);
                case 8:
                    return getResources().getDrawable(R.drawable.g8);
                case 9:
                    return getResources().getDrawable(R.drawable.g9);
                case 10:
                    return getResources().getDrawable(R.drawable.g10);
            }
        } else if (card.getModifier() == 2) {
            switch (card.getValue()) {
                case 1:
                    return getResources().getDrawable(R.drawable.b1);
                case 2:
                    return getResources().getDrawable(R.drawable.b2);
                case 3:
                    return getResources().getDrawable(R.drawable.b3);
                case 4:
                    return getResources().getDrawable(R.drawable.b4);
                case 5:
                    return getResources().getDrawable(R.drawable.b5);
                case 6:
                    return getResources().getDrawable(R.drawable.b6);
                case 7:
                    return getResources().getDrawable(R.drawable.b7);
                case 8:
                    return getResources().getDrawable(R.drawable.b8);
                case 9:
                    return getResources().getDrawable(R.drawable.b9);
                case 10:
                    return getResources().getDrawable(R.drawable.b10);
            }
        } else if (card.getModifier() == 3) {
            switch (card.getValue()) {
                case 1:
                    return getResources().getDrawable(R.drawable.r1);
                case 2:
                    return getResources().getDrawable(R.drawable.r2);
                case 3:
                    return getResources().getDrawable(R.drawable.r3);
                case 4:
                    return getResources().getDrawable(R.drawable.r4);
                case 5:
                    return getResources().getDrawable(R.drawable.r5);
                case 6:
                    return getResources().getDrawable(R.drawable.r6);
                case 7:
                    return getResources().getDrawable(R.drawable.r7);
                case 8:
                    return getResources().getDrawable(R.drawable.r8);
                case 9:
                    return getResources().getDrawable(R.drawable.r9);
                case 10:
                    return getResources().getDrawable(R.drawable.r10);
            }
        }
        return null;
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
            if (BruteForceGuard.isImprisoned()) {
                if (notifyMistake) {
                    final Toast toast = Toast.makeText(
                            GameActivity.this.getApplicationContext(),
                            "Stop cheating!",
                            Toast.LENGTH_SHORT
                    );
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);
                    toast.show();
                    mVibrator.vibrate(LONG_VIBRATE_TIME_MS);
                }
                notifyMistake = false;
                return;
            }
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
                playSound(mSoundCardPlaceId);
                MoveRequest moveRequest = new MoveRequest();
                moveRequest.setGameId(mGameId);
                moveRequest.setMove(card);
                moveRequest.setAuthToken(SessionInfo.getAuthToken());
                moveRequest.setMoveNumber(mMoveNumber);
                startService(NetworkService.getSendIntent(getApplicationContext(),
                        moveRequest, true));
                view.setVisibility(View.INVISIBLE);
                view.setOnClickListener(null);
                mNumOfCardsOnDesk--;
                int cardViewCoordinates[] = new int[2];
                view.getLocationOnScreen(cardViewCoordinates);
                int topCardCoordinates[] = new int[2];
                mTopCardImageButton.getLocationOnScreen(topCardCoordinates);
                TranslateAnimation animation = new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE,
                        cardViewCoordinates[0] - topCardCoordinates[0],
                        TranslateAnimation.RELATIVE_TO_SELF,
                        0,
                        TranslateAnimation.ABSOLUTE,
                        cardViewCoordinates[1] - topCardCoordinates[1],
                        TranslateAnimation.RELATIVE_TO_SELF,
                        0
                );
                animation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);
                mCardUnderTopCardImageButton.setImageDrawable(mTopCardImageButton.getDrawable());
                mTopCardImageButton.setImageDrawable(getDrawableCard(card));
                mTopCardImageButton.startAnimation(animation);
                BruteForceGuard.forgive();
            } else {
                BruteForceGuard.recordMistake();
                mVibrator.vibrate(VIBRATE_TIME_MS);
                notifyMistake = true;
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
                    NetworkService.setHandler(null);
                    Intent nextActivity = GameOverActivity.newIntent(getApplicationContext(), SessionInfo.getUserName(),
                            newStateNotification.getGameResult().getWinner(), newStateNotification.getGameResult().getScores());
                    startActivity(nextActivity);
                    finish();
                } else {
                    String moveWinner = newStateNotification.getMoveWinner();
                    if (moveWinner != null && !moveWinner.equals(SessionInfo.getUserName())) {
                        int topCardButtonCoordinates[] = new int[2];
                        mTopCardImageButton.getLocationOnScreen(topCardButtonCoordinates);
                        int playerDeckCoordinates[] = new int[2];
                        TranslateAnimation animation;
                        if (moveWinner.equals(mFirstPlayerNameTextView.getText().toString())) {
                            mFirstPlayerCardsNumTextView.setText(String.valueOf(Integer.parseInt(
                                    mFirstPlayerCardsNumTextView.getText().toString()) - 1));
                            mFirstPlayerDeck.getLocationOnScreen(playerDeckCoordinates);
                            animation = new TranslateAnimation(
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[0] - topCardButtonCoordinates[0],
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0,
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[1] - topCardButtonCoordinates[1],
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0
                            );
                            animation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);
                            mTopCardImageButton.startAnimation(animation);
                        } else if (moveWinner.equals(mSecondPlayerNameTextView.getText().toString())) {
                            mSecondPlayerCardsNumTextView.setText(String.valueOf(Integer.parseInt(
                                    mSecondPlayerCardsNumTextView.getText().toString()) - 1));
                            mSecondPlayerDeck.getLocationOnScreen(playerDeckCoordinates);
                            animation = new TranslateAnimation(
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[0] - topCardButtonCoordinates[0],
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0,
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[1] - topCardButtonCoordinates[1],
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0
                            );
                            animation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);
                            mTopCardImageButton.startAnimation(animation);
                        } else if (moveWinner.equals(mThirdPlayerNameTextView.getText().toString())) {
                            mThirdPlayerCardsNumTextView.setText(String.valueOf(Integer.parseInt(
                                    mThirdPlayerCardsNumTextView.getText().toString()) - 1));
                            mThirdPlayerDeck.getLocationOnScreen(playerDeckCoordinates);
                            animation = new TranslateAnimation(
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[0] - topCardButtonCoordinates[0]
                                            - mThirdPlayerDeck.getWidth(),
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0,
                                    TranslateAnimation.ABSOLUTE,
                                    playerDeckCoordinates[1] - topCardButtonCoordinates[1],
                                    TranslateAnimation.RELATIVE_TO_SELF,
                                    0
                            );
                            animation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);
                            /*RotateAnimation rotateAnimation = new RotateAnimation(0, 90,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);*/
                            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.animation_card_rotate);
                            rotateAnimation.setDuration(CARD_DISTRIBUTION_ANIMATION_DURATION_MILLIS);
                            AnimationSet animationSet = new AnimationSet(false);
                            animationSet.addAnimation(rotateAnimation);
                            animationSet.addAnimation(animation);
                            mTopCardImageButton.startAnimation(animationSet);
                        }
                        playSound(mSoundCardSlideOpponentId);
                        mCardUnderTopCardImageButton.setImageDrawable(mTopCardImageButton.getDrawable());
                        mTopCardImageButton.bringToFront();
                    }
                    mTopCard = newStateNotification.getNextCard();
                    mMoveNumber = newStateNotification.getMoveNumber();
                    mTopCardImageButton.setImageDrawable(getDrawableCard(mTopCard));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave game")
                .setMessage("Do you really want to leave game?")
                //android.R.string.yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LeaveGameRequest leaveGameRequest = new LeaveGameRequest();
                        leaveGameRequest.setAuthToken(SessionInfo.getAuthToken());
                        leaveGameRequest.setGameId(SessionInfo.getGameId());

                        startService(NetworkService.getSendIntent(getApplicationContext(),
                                leaveGameRequest, true));

                        Context context = GameActivity.this.getApplicationContext();
                        Intent nextActivity = LobbyListActivity.getStartIntent(context);
                        context.startActivity(nextActivity);
                    }
                })
                .setNegativeButton("No", null).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicService.getInstance(this.getApplicationContext()).stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
    }
}
