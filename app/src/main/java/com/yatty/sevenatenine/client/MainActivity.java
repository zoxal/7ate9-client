package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.yatty.sevenatenine.api.in_commands.ErrorResponse;
import com.yatty.sevenatenine.api.in_commands.LogInResponse;
import com.yatty.sevenatenine.api.out_commands.LogInRequest;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mConnectButton;
    private EditText mNameEditText;
    private NettyClient mNettyClient;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        mConnectButton = findViewById(R.id.button_connect);
        mNameEditText = findViewById(R.id.et_name);

        final MainActivityHandler mainActivityHandler = new MainActivityHandler(this, mNameEditText,
                mConnectButton);
        mNettyClient = NettyClient.getInstance();
        mNettyClient.setHandler(mainActivityHandler);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!mNameEditText.getText().toString().isEmpty()) {
                        LogInRequest logInRequest = new LogInRequest();
                        logInRequest.setName(mNameEditText.getText().toString());
                        mNettyClient.write(logInRequest, false);
                        view.setClickable(false);
                        Snackbar.make(view, "Connecting...", Snackbar.LENGTH_SHORT).show();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    } else {
                        Snackbar.make(view, "Enter name.", Snackbar.LENGTH_SHORT).show();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        public void handleMessage(Message msg) {
            Log.d(TAG, "MainActivity.Handler: Get obj: " + msg.obj);
            if (msg.obj instanceof LogInResponse) {
                LogInResponse logInResponse = (LogInResponse) msg.obj;
                UserInfo.setAuthToken(logInResponse.getAuthToken());
                UserInfo.setUserName(nameEditText.getText().toString());
                Log.d(TAG, "Connected");
                NettyClient.getInstance().setHandler(null);
                Intent nextActivity = LobbyListActivity.getStartIntent(context);
                context.startActivity(nextActivity);
                appCompatActivity.finish();
            } else if (msg.obj instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) msg.obj;
                Toast.makeText(context, errorResponse.getShortDescription(), Toast.LENGTH_LONG);
                connectButton.setClickable(true);
            }
        }
    }
}
