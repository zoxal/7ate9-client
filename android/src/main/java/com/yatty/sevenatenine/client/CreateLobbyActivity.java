package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;

public class CreateLobbyActivity extends AppCompatActivity {
    public static final String TAG = CreateLobbyActivity.class.getSimpleName();

    private Spinner mPlayersNumberSpinner;
    private EditText mLobbyNameEditText;
    private Button mCreateLobbyButton;

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
                CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest();
                createLobbyRequest.setLobbyName(mLobbyNameEditText.getText().toString());
                createLobbyRequest.setMaxPlayersNumber(Integer.parseInt(mPlayersNumberSpinner.getSelectedItem().toString()));
                createLobbyRequest.setAuthToken(UserInfo.getAuthToken());
                Intent intentWithData = LobbyListActivity.getIntentWithData(createLobbyRequest);
                setResult(RESULT_OK, intentWithData);
                finish();
            }
        });
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CreateLobbyActivity.class);
        return intent;
    }
}
