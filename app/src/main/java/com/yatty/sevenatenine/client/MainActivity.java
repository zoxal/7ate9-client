package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yatty.sevenatenine.api.in_commands.ConnectResponse;
import com.yatty.sevenatenine.api.out_commands.ConnectRequest;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    private Button connectButton;
    private EditText nameEditText;
    private NettyClient nettyClient;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = findViewById(R.id.connect_button);
        nameEditText = findViewById(R.id.name_edit_text);

        final MainActivityHandler mainActivityHandler = new MainActivityHandler(this, nameEditText,
                connectButton);
        nettyClient = NettyClient.getInstance(mainActivityHandler);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ConnectRequest connectRequest = new ConnectRequest();
                    connectRequest.setName(nameEditText.getText().toString());
                    nettyClient.write(connectRequest);
                    view.setClickable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static class MainActivityHandler extends Handler {
        private Context context;
        private EditText nameEditText;
        private Button connectButton;
        private AppCompatActivity appCompatActivity;

        MainActivityHandler(AppCompatActivity appCompatActivity, EditText nameEditText, Button connectButton) {
            context = appCompatActivity.getApplicationContext();
            this.nameEditText = nameEditText;
            this.appCompatActivity = appCompatActivity;
            this.connectButton = connectButton;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(TAG, "MainActivity.Handler: Get obj: " + msg.obj);
            if (msg.sendingUid == ConnectResponse.UID) {
                ConnectResponse connectResponse = (ConnectResponse) msg.obj;
                if (connectResponse.isSucceed()) {
                    String gameId = connectResponse.getGameId();
                    Log.d(TAG, "Connected");
                    // Test this method call!!!
                    NettyClient.getInstance(null);
                    Intent nextActivity = GameActivity.newIntent(context, gameId,
                            nameEditText.getText().toString());
                    context.startActivity(nextActivity);
                    appCompatActivity.finish();
                } else {
                    connectButton.setClickable(true);
                    Toast.makeText(context, "Connection refused", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
