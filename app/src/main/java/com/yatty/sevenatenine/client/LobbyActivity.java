package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PlayerInfo;
import com.yatty.sevenatenine.api.commands_with_data.PlayerResult;
import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;

public class LobbyActivity extends AppCompatActivity {
    private static final String EXTRA_PRIVATE_LOBBY_INFO = "private_lobby_info";

    private PrivateLobbyInfo mPrivateLobbyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

    }

    public Intent getStartIntent(Context context, PrivateLobbyInfo privateLobbyInfo) {
        Intent intent = new Intent(context, LobbyActivity.class);
        intent.putExtra(EXTRA_PRIVATE_LOBBY_INFO, privateLobbyInfo);
        return intent;
    }

    private void retrieveInfoFromIntent() {
        Intent intent = getIntent();
        mPrivateLobbyInfo = intent.getParcelableExtra(EXTRA_PRIVATE_LOBBY_INFO);
    }
}
