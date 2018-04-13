package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.out_commands.LeaveLobbyRequest;

public class LobbyActivity extends AppCompatActivity {
    private static final String EXTRA_PRIVATE_LOBBY_INFO = "private_lobby_info";
    private static final String EXTRA_LOBBY_ID = "lobby_id";

    private TextView mPlayersNumberTextView;
    private ImageButton mLeaveLobbyImageButton;
    private String mLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mLobbyId = getIntent().getExtras().getString(EXTRA_LOBBY_ID);
        setContentView(R.layout.activity_lobby);
        mLeaveLobbyImageButton = findViewById(R.id.ib_leave_lobby);
        mPlayersNumberTextView = findViewById(R.id.tv_lobby_players);
        mLeaveLobbyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveLobbyRequest leaveLobbyRequest = new LeaveLobbyRequest(mLobbyId);
                NettyClient.getInstance().write(leaveLobbyRequest, false);
                Intent lobbyListActivityIntent = LobbyListActivity.getStartIntent(getApplicationContext());
                startActivity(lobbyListActivityIntent);
                finish();
            }
        });
        LobbyActivityHandler lobbyActivityHandler = new LobbyActivityHandler();
        NettyClient.getInstance().setHandler(lobbyActivityHandler);
    }

    public static Intent getStartIntent(Context context, PrivateLobbyInfo privateLobbyInfo, String lobbyId) {
        Intent intent = new Intent(context, LobbyActivity.class);
        intent.putExtra(EXTRA_PRIVATE_LOBBY_INFO, privateLobbyInfo);
        intent.putExtra(EXTRA_LOBBY_ID, lobbyId);
        return intent;
    }

    private class LobbyActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof GameStartedNotification) {
                GameStartedNotification gameStartedNotification = (GameStartedNotification) msg.obj;
                NettyClient.getInstance().setHandler(null);
                Intent intent = GameActivity.getStartIntent(getApplicationContext(), gameStartedNotification);
                startActivity(intent);
                finish();
            } else if (msg.obj instanceof PrivateLobbyInfo) {
                PrivateLobbyInfo privateLobbyInfo = (PrivateLobbyInfo) msg.obj;
                SessionInfo.setPrivateLobbyInfo(privateLobbyInfo);
                mPlayersNumberTextView.setText(String.valueOf(privateLobbyInfo.getPlayers().length));
            }
        }
    }
}
