package com.yatty.sevenatenine.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    public static final String CONNECT_MESSAGE = "connect";

    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = findViewById(R.id.connect_button);
        final Handler h = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "msg what: " + msg.what);
                Log.d(TAG, "Connected");
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT);
                Intent nextActivity = GameActivity.newIntent(getApplicationContext());
                startActivity(nextActivity);
                finish();
            }
        };
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    NettyUdpClient nettyUdpClient = new NettyUdpClient();
                    nettyUdpClient.sendOneMessage(CONNECT_MESSAGE, h);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
