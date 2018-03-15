package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.GameStartedEvent;

public class LobbyActivity extends AppCompatActivity {
    private static final String EXTRA_PRIVATE_LOBBY_INFO = "private_lobby_info";

    private PrivateLobbyInfo mPrivateLobbyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        LobbyActivityHandler lobbyActivityHandler = new LobbyActivityHandler();
        NettyClient.getInstance().setHandler(lobbyActivityHandler);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LobbyActivity.class);
        return intent;
    }

    public static Intent getStartIntent(Context context, PrivateLobbyInfo privateLobbyInfo) {
        Intent intent = new Intent(context, LobbyActivity.class);
        intent.putExtra(EXTRA_PRIVATE_LOBBY_INFO, privateLobbyInfo);
        return intent;
    }

    private void retrieveInfoFromIntent() {
        Intent intent = getIntent();
        mPrivateLobbyInfo = intent.getParcelableExtra(EXTRA_PRIVATE_LOBBY_INFO);
    }

    private class LobbyActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof GameStartedEvent) {
                GameStartedEvent gameStartedEvent = (GameStartedEvent) msg.obj;
                Intent intent = GameActivity.getStartIntent(getApplicationContext(), gameStartedEvent);
                NettyClient.getInstance().setHandler(null);
                startActivity(intent);
                finish();
            }
        }
    }
}
