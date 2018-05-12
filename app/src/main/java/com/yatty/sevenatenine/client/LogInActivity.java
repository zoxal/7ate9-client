package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.yatty.sevenatenine.api.in_commands.ErrorResponse;
import com.yatty.sevenatenine.api.in_commands.LogInResponse;
import com.yatty.sevenatenine.api.out_commands.LogInRequest;
import com.yatty.sevenatenine.client.auth.AuthManager;
import com.yatty.sevenatenine.client.auth.SessionInfo;
import com.yatty.sevenatenine.client.network.NetworkService;

public class LogInActivity extends AppCompatActivity {
    public static final String TAG = LogInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private Button mConnectButton;
    private EditText mNameEditText;
    private boolean shouldMusicStay;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_log_in);
        mConnectButton = findViewById(R.id.button_log_in);
        mNameEditText = findViewById(R.id.et_name);
        mHandler = new LogInHandler(this, mNameEditText, mConnectButton);
        mConnectButton.setOnLongClickListener(v -> {
            DialogFragment dialogFragment = new IpSettingsDialog();
            dialogFragment.show(getSupportFragmentManager(), "Enter IP");
            return false;
        });
    }

    public void connectButtonClicked(View view) {
        try {
            if (!NetworkService.isOnline(getApplicationContext())) {
                showSnackbar("No connection.");
                return;
            }
            if (!mNameEditText.getText().toString().isEmpty()) {
                view.setEnabled(false);
                showSnackbar("Connecting...");
                enterServer(
                        mNameEditText.getText().toString(),
                        AuthManager.getUniqueDeviceHash(getApplicationContext())
                );
            } else {
                showSnackbar("Enter nickname.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during connect", e);
        }
    }

    public void googleSignInClicked(View view) {
        if (!NetworkService.isOnline(getApplicationContext())) {
            showSnackbar("No connection.");
            return;
        }
        view.setEnabled(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        try {
            if (account != null) {
                String personId = account.getId();
                enterServer(account);
                Log.d(TAG, "Acc id: " + personId);
            } else {
                Log.d(TAG, "No acc");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
            view.setEnabled(true);
        } catch (Exception e) {
            view.setEnabled(true);
            showSnackbar("Failed to connect");
            Log.e(TAG, "Failed to connect to server", e);
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void enterServer(GoogleSignInAccount account) {
        enterServer(account.getDisplayName(), AuthManager.getSHAHash(account.getId()));
    }

    private void enterServer(String name, String passwordHash) {
        Log.d(TAG, "Connecting to server...");
        startService(NetworkService.getConnectionIntent(getApplicationContext()));
        LogInRequest logInRequest = new LogInRequest();
        logInRequest.setName(name);
        logInRequest.setPasswordHash(passwordHash);
        NetworkService.setHandler(mHandler);
        Log.d(TAG, "Sending login request");
        startService(NetworkService.getSendIntent(getApplicationContext(), logInRequest, false));
    }

    private void showSnackbar(String title) {
        FrameLayout parentFrameLayout = findViewById(R.id.fl_parent);
        Snackbar snackbar = Snackbar.make(parentFrameLayout, title, Snackbar.LENGTH_SHORT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d(TAG, "getting google account");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Got acc width id: " + account.getId());
                Log.d(TAG, "Acc display name: " + account.getDisplayName());
                Log.d(TAG, "Acc family name: " + account.getFamilyName());
                Log.d(TAG, "Acc given name: " + account.getGivenName());
                showSnackbar("winner-winner!");

                if (account.getId() == null) {
                    Log.e(TAG, "Failed to get Google Account id");
                    showSnackbar("Failed to connect to Google Account");
                }
                enterServer(account);
            } catch (Exception e) {
                showSnackbar("Failed to connect to Google Account");
                Log.e(TAG, "Exception", e);
            }
        }
    }

    class LogInHandler extends Handler {
        private Context context;
        private EditText nameEditText;
        private Button connectButton;
        private AppCompatActivity appCompatActivity;

        LogInHandler(AppCompatActivity appCompatActivity, EditText nameEditText, Button connectButton) {
            context = appCompatActivity.getApplicationContext();
            this.nameEditText = nameEditText;
            this.appCompatActivity = appCompatActivity;
            this.connectButton = connectButton;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "LogInActivity.Handler: Get obj: " + msg.obj);
            if (msg.obj instanceof LogInResponse) {
                LogInResponse logInResponse = (LogInResponse) msg.obj;
                SessionInfo.setAuthToken(logInResponse.getAuthToken());
                SessionInfo.setUserName(nameEditText.getText().toString());
                Log.d(TAG, "Connected");
                NetworkService.setHandler(null);
                Intent nextActivity = LobbyListActivity.getStartIntent(context);
                context.startActivity(nextActivity);
                shouldMusicStay = true;
                appCompatActivity.finish();
            } else if (msg.obj instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) msg.obj;
                Toast.makeText(context, errorResponse.getShortDescription(), Toast.LENGTH_LONG);
                connectButton.setClickable(true);
            }
        }
    }
}