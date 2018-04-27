package com.yatty.sevenatenine.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.in_commands.LobbyStateChangedNotification;
import com.yatty.sevenatenine.api.out_commands.LeaveGameRequest;
import com.yatty.sevenatenine.client.network.NetworkService;

public class LobbyActivity extends AppCompatActivity {
    private static final String TAG = LobbyActivity.class.getSimpleName();
    private static final String EXTRA_PRIVATE_LOBBY_INFO = "private_lobby_info";
    private static final String EXTRA_LOBBY_ID = "lobby_id";

    private TextView mPlayersNumberTextView;
    private String mLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mLobbyId = getIntent().getExtras().getString(EXTRA_LOBBY_ID);
        setContentView(R.layout.activity_lobby);
        mPlayersNumberTextView = findViewById(R.id.tv_lobby_players);
        LobbyActivityHandler lobbyActivityHandler = new LobbyActivityHandler();
        NetworkService.setHandler(lobbyActivityHandler);
    }

    public static Intent getStartIntent(Context context, PrivateLobbyInfo privateLobbyInfo, String lobbyId) {
        Intent intent = new Intent(context, LobbyActivity.class);
        intent.putExtra(EXTRA_PRIVATE_LOBBY_INFO, (Parcelable) privateLobbyInfo);
        intent.putExtra(EXTRA_LOBBY_ID, lobbyId);
        return intent;
    }

    private class LobbyActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof GameStartedNotification) {
                GameStartedNotification gameStartedNotification = (GameStartedNotification) msg.obj;
                NetworkService.setHandler(null);
                Intent intent = GameActivity.getStartIntent(getApplicationContext(), gameStartedNotification);
                startActivity(intent);
                finish();
            } else if (msg.obj instanceof LobbyStateChangedNotification) {
                Log.d(TAG, "Got LobbyStateChangedNotification");
                LobbyStateChangedNotification lobbyStateChangedNotification = (LobbyStateChangedNotification) msg.obj;
                if (lobbyStateChangedNotification.getPrivateLobbyInfo() == null) {
                    Log.d(TAG, "Private lobby info is null!!!!");
                }
                SessionInfo.setPrivateLobbyInfo(lobbyStateChangedNotification.getPrivateLobbyInfo());
                mPlayersNumberTextView.setText(String.valueOf(lobbyStateChangedNotification
                        .getPrivateLobbyInfo().getPlayers().length));
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave lobby")
                .setMessage("Do you really want to leave lobby?")
                //android.R.string.yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LeaveGameRequest leaveGameRequest = new LeaveGameRequest();
                        leaveGameRequest.setAuthToken(SessionInfo.getAuthToken());
                        leaveGameRequest.setGameId(SessionInfo.getGameId());

                        startService(NetworkService.getSendIntent(getApplicationContext(),
                                leaveGameRequest, true));

                        Context context = getApplicationContext();
                        Intent nextActivity = LobbyListActivity.getStartIntent(context);
                        context.startActivity(nextActivity);
                        finish();
                    }
                })
                .setNegativeButton("No", null).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicService.getInstance(this.getApplicationContext()).pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApplicationSettings.isMusicEnabled(this)) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).start();
        }
    }
}
