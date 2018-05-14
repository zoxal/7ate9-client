package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;
import com.yatty.sevenatenine.client.auth.SessionInfo;

public class CreateLobbyActivity extends AppCompatActivity {
    public static final String TAG = CreateLobbyActivity.class.getSimpleName();

    private Spinner mPlayersNumberSpinner;
    private EditText mLobbyNameEditText;
    private Button mCreateLobbyButton;
    private boolean shouldMusicStay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        mPlayersNumberSpinner = findViewById(R.id.spinner_players_number);
        mLobbyNameEditText = findViewById(R.id.et_lobby_name);
        mCreateLobbyButton = findViewById(R.id.button_create_lobby);
        mCreateLobbyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mLobbyNameEditText.getText().toString().isEmpty()) {
                    CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest();
                    createLobbyRequest.setLobbyName(mLobbyNameEditText.getText().toString());
                    createLobbyRequest.setMaxPlayersNumber(Integer.parseInt(mPlayersNumberSpinner.getSelectedItem().toString()));
                    createLobbyRequest.setAuthToken(SessionInfo.getAuthToken());
                    Intent intentWithData = LobbyListActivity.getIntentWithData(getApplicationContext(), createLobbyRequest);
                    setResult(RESULT_OK, intentWithData);
                    shouldMusicStay = true;
                    finish();
                } else {
                    showSnackbar("Enter lobby name.");
                }
            }
        });
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CreateLobbyActivity.class);
        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!shouldMusicStay) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(ApplicationSettings.getBackgroundPicture(this));
        if (ApplicationSettings.isMusicEnabled(this)) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).start();
        }
    }

    private void showSnackbar(String title) {
        RelativeLayout parentRelativeLayout = findViewById(R.id.rl_parent);
        Snackbar snackbar = Snackbar.make(parentRelativeLayout,
                title, Snackbar.LENGTH_SHORT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
