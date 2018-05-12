package com.yatty.sevenatenine.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = findViewById(android.R.id.content);
        view.setBackground(ApplicationSettings.getBackgroundPicture(this));
        setContentView(R.layout.activity_main2);
    }
}
