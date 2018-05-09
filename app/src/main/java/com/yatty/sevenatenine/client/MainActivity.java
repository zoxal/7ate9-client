package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.yatty.sevenatenine.api.in_commands.ErrorResponse;
import com.yatty.sevenatenine.api.in_commands.LogInResponse;
import com.yatty.sevenatenine.api.out_commands.LogInRequest;
import com.yatty.sevenatenine.client.network.NetworkService;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private Button mConnectButton;
    private EditText mNameEditText;
    private ImageButton mSingInViewGoogleAccImageButton;
    private boolean shouldMusicStay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        mConnectButton = findViewById(R.id.button_connect);
        mNameEditText = findViewById(R.id.et_name);
        mSingInViewGoogleAccImageButton = findViewById(R.id.ib_sing_in_via_google_acc);

        final MainActivityHandler mainActivityHandler = new MainActivityHandler(this, mNameEditText,
                mConnectButton);
        NetworkService.setHandler(mainActivityHandler);

        mConnectButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                DialogFragment dialogFragment = new IpSettingsDialog();
                dialogFragment.show(getSupportFragmentManager(), "Enter IP");
                return false;
            }
        });

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Connecting..");
                try {
                    if (!isOnline()) {
                        Log.d(TAG, "Status: offline");
                        showSnackbar("No connection.");
                        return;
                    }
                    if (!mNameEditText.getText().toString().isEmpty()) {
                        startService(NetworkService.getConnectionIntent(getApplicationContext()));
                        LogInRequest logInRequest = new LogInRequest();
                        logInRequest.setName(mNameEditText.getText().toString());
                        logInRequest.setPasswordHash(getPasswordHashViaMac());
//                        startService(NetworkService.getSendIntent(getApplicationContext(),
//                                logInRequest, false));
                        view.setClickable(false);
                        showSnackbar("Connecting...");
                    } else {
                        showSnackbar("Enter nickname.");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception during connect", e);
                }
            }
        });

        mSingInViewGoogleAccImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPasswordHashViaGoggleAcc();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicService.getInstance(this.getApplicationContext()).pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfo != null && netInfo.isConnected()) || (wifiInfo != null && wifiInfo.isConnected());
    }

    private String getPasswordHashViaMac() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(address.getBytes(Charset.forName("UTF-8")));
            hash = new String(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "Hash by MAC: " + hash);
        return hash;
    }

    private String getPasswordHashViaGoggleAcc() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personId = acct.getId();
            Log.d(TAG, "Acc id: " + personId);
        } else{
            Log.d(TAG, "No acc");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        return "Hash";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Inside IF");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d(TAG, "getting google account");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Got acc width id: " + account.getId());
                Log.d(TAG, "Acc display name: " + account.getDisplayName());
                Log.d(TAG, "Acc family name: " + account.getFamilyName());
                Log.d(TAG, "Acc given name: " + account.getGivenName());
            } catch (ApiException e) {
                Log.d(TAG, "Exception", e);
            }
        }
    }

    private void showSnackbar(String title) {
        FrameLayout parentFrameLayout = findViewById(R.id.fl_parent);
        Snackbar snackbar = Snackbar.make(parentFrameLayout,
                title, Snackbar.LENGTH_SHORT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }

    class MainActivityHandler extends Handler {
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
