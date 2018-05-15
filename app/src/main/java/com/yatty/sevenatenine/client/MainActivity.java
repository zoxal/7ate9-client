package com.yatty.sevenatenine.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button logInButton = findViewById(R.id.button_log_in);
        logInButton.setOnClickListener((view) -> {
            Intent logInIntent = LogInActivity.getStartIntent(this);
            startActivity(logInIntent);
            finish();
        });

        final Button settingsButton = findViewById(R.id.button_setting);
        settingsButton.setOnClickListener((view) -> {
            Intent settingIntent = SettingsActivity.getStartIntent(this);
            startActivity(settingIntent);
        });

        final Button exitButton = findViewById(R.id.button_exit);
        exitButton.setOnClickListener((view) -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(ApplicationSettings.getBackgroundPicture(this));
    }
}
