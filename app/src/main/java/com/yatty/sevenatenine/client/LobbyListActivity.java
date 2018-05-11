package com.yatty.sevenatenine.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatty.sevenatenine.api.commands_with_data.PlayerInfo;
import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.CreateLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.EnterLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.LobbyListUpdatedNotification;
import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.EnterLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListSubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListUnsubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LogOutRequest;
import com.yatty.sevenatenine.client.auth.SessionInfo;
import com.yatty.sevenatenine.client.network.NetworkService;

import java.util.ArrayList;

public class LobbyListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = LobbyListActivity.class.getSimpleName();
    private static final String EXTRA_CREATE_LOBBY_REQUEST = "create_lobby_request";

    private FloatingActionButton mAddFloatingActionButton;
    private FloatingActionButton mSettingsFloatingActionButton;
    private RecyclerView mLobbyListRecyclerView;
    private TextView mEmptyLobbyListTextView;
    private LobbyAdapter mLobbyAdapter;
    private boolean shouldMusicStay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);
        mLobbyListRecyclerView = findViewById(R.id.rv_lobby_list);
        mSettingsFloatingActionButton = findViewById(R.id.fab_settings);
        mEmptyLobbyListTextView = findViewById(R.id.tv_empty_lobby_list);
        mLobbyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LobbyListActivityHandler lobbyListActivityHandler =
                new LobbyListActivityHandler();
        NetworkService.setHandler(lobbyListActivityHandler);
        mAddFloatingActionButton = findViewById(R.id.fab_add_lobby);
        mAddFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = CreateLobbyActivity.getStartIntent(getApplicationContext());
                startActivityForResult(startIntent, REQUEST_CODE);
            }
        });
        mSettingsFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = SettingsActivity.getStartIntent(getApplicationContext());
                startActivity(intent);
            }
        });
        LobbyListSubscribeRequest lobbyListSubscribeRequest = new LobbyListSubscribeRequest(SessionInfo.getAuthToken());
        startService(NetworkService.getSendIntent(getApplicationContext(),
                lobbyListSubscribeRequest, true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (data == null) {
            Log.d(TAG, "data is Null!!!!!!!!!!");
            return;
        }
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "sending creating");
            CreateLobbyRequest createLobbyRequest = (CreateLobbyRequest) data
                    .getSerializableExtra(EXTRA_CREATE_LOBBY_REQUEST);
            PublicLobbyInfo publicLobbyInfo = new PublicLobbyInfo();
            publicLobbyInfo.setLobbyName(createLobbyRequest.getLobbyName());
            publicLobbyInfo.setMaxPlayersNumber(createLobbyRequest.getMaxPlayersNumber());
            publicLobbyInfo.setCurrentPlayersNumber(1);
            SessionInfo.setPublicLobbyInfo(publicLobbyInfo);
            startService(NetworkService.getSendIntent(getApplicationContext(),
                    createLobbyRequest, true));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!shouldMusicStay) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApplicationSettings.isMusicEnabled(this)) {
            BackgroundMusicService.getInstance(this.getApplicationContext()).start();
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LobbyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(EXTRA_AUTH_TOKEN, authToken);
        return intent;
    }

    public static Intent getIntentWithData(Context context, CreateLobbyRequest createLobbyRequest) {
        Intent intent = new Intent(context, LobbyListActivity.class);
        intent.putExtra(EXTRA_CREATE_LOBBY_REQUEST, createLobbyRequest);
        return intent;
    }

    private void updateLobbyList(ArrayList<PublicLobbyInfo> lobbyInfoArrayList) {
        if (mLobbyAdapter == null) {
            if (lobbyInfoArrayList.isEmpty()) {
                mEmptyLobbyListTextView.setVisibility(View.VISIBLE);
            }
            mLobbyAdapter = new LobbyAdapter(lobbyInfoArrayList);
            mLobbyListRecyclerView.setAdapter(mLobbyAdapter);
        } else {
            if (lobbyInfoArrayList.isEmpty()) {
                mEmptyLobbyListTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyLobbyListTextView.setVisibility(View.INVISIBLE);
            }
            mLobbyAdapter.setLobbyInfoArrayList(lobbyInfoArrayList);
            mLobbyAdapter.notifyDataSetChanged();
        }
    }

    private class LobbyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String DIVIDER = "/";

        private TextView mLobbyNameTextView;
        private TextView mLobbyPlayersTextView;
        private PublicLobbyInfo mPublicLobbyInfo;

        public LobbyHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLobbyNameTextView = itemView.findViewById(R.id.tv_lobby_name);
            mLobbyPlayersTextView = itemView.findViewById(R.id.tv_lobby_players);
        }

        public void bindLobby(PublicLobbyInfo publicLobbyInfo) {
            Log.d(TAG, "bindLobby: " + publicLobbyInfo.getLobbyName());
            mPublicLobbyInfo = publicLobbyInfo;
            mLobbyNameTextView.setText(mPublicLobbyInfo.getLobbyName());
            mLobbyPlayersTextView.setText(mPublicLobbyInfo.getCurrentPlayersNumber() +
                    DIVIDER + mPublicLobbyInfo.getMaxPlayersNumber());
        }

        @Override
        public void onClick(View v) {
            EnterLobbyRequest enterLobbyRequest = new EnterLobbyRequest(mPublicLobbyInfo.getLobbyId(),
                    SessionInfo.getAuthToken());
            SessionInfo.setPublicLobbyInfo(mPublicLobbyInfo);
            startService(NetworkService.getSendIntent(getApplicationContext(),
                    enterLobbyRequest, true));

        }
    }

    private class LobbyAdapter extends RecyclerView.Adapter<LobbyHolder> {

        private ArrayList<PublicLobbyInfo> mLobbyInfoArrayList;

        public LobbyAdapter(ArrayList<PublicLobbyInfo> lobbyInfoArrayList) {
            mLobbyInfoArrayList = lobbyInfoArrayList;
        }

        @NonNull
        @Override
        public LobbyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.item_lobby, parent, false);
            return new LobbyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LobbyHolder holder, int position) {
            PublicLobbyInfo lobbyInfo = mLobbyInfoArrayList.get(position);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Binding lobbye: " + lobbyInfo.getLobbyName());
            }
            holder.bindLobby(lobbyInfo);
        }

        public void setLobbyInfoArrayList(ArrayList<PublicLobbyInfo> lobbyInfoArrayList) {
            mLobbyInfoArrayList = lobbyInfoArrayList;
        }

        @Override
        public int getItemCount() {
            if (mLobbyInfoArrayList == null) return 0;
            return mLobbyInfoArrayList.size();
        }
    }

    private class LobbyListActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "LobbyListActivityHandler called");
            if (msg.obj instanceof LobbyListUpdatedNotification) {
                Log.d(TAG, "LobbyListActivityHandler: LobbyListUpdatedNotification");
                LobbyListUpdatedNotification lobbyListUpdatedNotification = (LobbyListUpdatedNotification) msg.obj;
                if (lobbyListUpdatedNotification.getLobbyList() == null) {
                    Log.d(TAG, "getLobbyList is null");
                }
                updateLobbyList(lobbyListUpdatedNotification.getLobbyList());
            } else if (msg.obj instanceof EnterLobbyResponse) {
                Log.d(TAG, "LobbyListActivityHandler: LobbyListUpdatedNotification");
                startService(NetworkService.getSendIntent(getApplicationContext(),
                        new LobbyListUnsubscribeRequest(SessionInfo.getAuthToken()), true));
                EnterLobbyResponse enterLobbyResponse = (EnterLobbyResponse) msg.obj;
                SessionInfo.setPrivateLobbyInfo(enterLobbyResponse.getPrivateLobbyInfo());
                NetworkService.setHandler(null);
                Intent nextActivity = LobbyActivity.getStartIntent(getApplicationContext());
                startActivity(nextActivity);
                shouldMusicStay = true;
                finish();
            } else if (msg.obj instanceof CreateLobbyResponse) {
                Log.d(TAG, "LobbyListActivityHandler: CreateLobbyResponse");
                startService(NetworkService.getSendIntent(getApplicationContext(),
                        new LobbyListUnsubscribeRequest(SessionInfo.getAuthToken()), true));
                CreateLobbyResponse createLobbyResponse = (CreateLobbyResponse) msg.obj;
                SessionInfo.getPublicLobbyInfo().setLobbyId(createLobbyResponse.getLobbyId());
                SessionInfo.setPrivateLobbyInfo(new PrivateLobbyInfo(new PlayerInfo(SessionInfo.getUserName())));
                NetworkService.setHandler(null);
                Intent intent = LobbyActivity.getStartIntent(getApplicationContext());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave server")
                .setMessage("Do you really want to leave server?")
                //android.R.string.yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LogOutRequest logOutRequest = new LogOutRequest(SessionInfo.getAuthToken());

                        startService(NetworkService.getSendIntent(getApplicationContext(),
                                logOutRequest, true));

                        Context context = LobbyListActivity.this.getApplicationContext();
                        Intent nextActivity = MainActivity.getStartIntent(context);
                        context.startActivity(nextActivity);
                        finish();
                    }
                })
                .setNegativeButton("No", null).show();
    }
}
