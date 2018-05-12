package com.yatty.sevenatenine.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import com.yatty.sevennine.util.codecs.JsonMessageEncoder;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JsonMessageEncoder jsonMessageEncoder = new JsonMessageEncoder();
        setContentView(R.layout.activity_main2);
    }
}
