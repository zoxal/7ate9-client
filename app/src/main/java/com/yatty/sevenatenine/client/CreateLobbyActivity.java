package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateLobbyActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = CreateLobbyActivity.class.getSimpleName().hashCode();

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
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CreateLobbyActivity.class);
        return intent;
    }
}
