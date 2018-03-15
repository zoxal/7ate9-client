package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

public class CreateLobbyActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = CreateLobbyActivity.class.getSimpleName().hashCode();

    Spinner mPlayersNumberSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        mPlayersNumberSpinner = findViewById(R.id.spinner_players_number);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, CreateLobbyActivity.class);
        return intent;
    }
}
