package com.yatty.sevenatenine.client;

import android.content.Context;
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

import com.yatty.sevenatenine.api.commands_with_data.PrivateLobbyInfo;
import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.CreateLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.EnterLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.LobbyListUpdatedNotification;
import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.EnterLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListSubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListUnsubscribeRequest;

import java.util.ArrayList;

public class LobbyListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = LobbyListActivity.class.getSimpleName();
    private static final String EXTRA_CREATE_LOBBY_REQUEST = "create_lobby_request";

    private FloatingActionButton mAddFloatingActionButton;
    private RecyclerView mLobbyListRecyclerView;
    private LobbyAdapter mLobbyAdapter;
    private NettyClient mNettyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);
        mLobbyListRecyclerView = findViewById(R.id.rv_lobby_list);
        mLobbyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LobbyListActivityHandler lobbyListActivityHandler =
                new LobbyListActivityHandler();
        mNettyClient = NettyClient.getInstance();
        mNettyClient.setHandler(lobbyListActivityHandler);
        mAddFloatingActionButton = findViewById(R.id.fab_add_lobby);
        mAddFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = CreateLobbyActivity.getStartIntent(getApplicationContext());
                startActivityForResult(startIntent, REQUEST_CODE);
            }
        });
        LobbyListSubscribeRequest lobbyListSubscribeRequest = new LobbyListSubscribeRequest(UserInfo.getAuthToken());
        mNettyClient.write(lobbyListSubscribeRequest, true);
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
            mNettyClient.write(createLobbyRequest, true);
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LobbyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(EXTRA_AUTH_TOKEN, authToken);
        return intent;
    }

    public static Intent getIntentWithData(CreateLobbyRequest createLobbyRequest) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CREATE_LOBBY_REQUEST, createLobbyRequest);
        return intent;
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
                    UserInfo.getAuthToken());
            mNettyClient.write(enterLobbyRequest, true);
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

    private void updateLobbyList(ArrayList<PublicLobbyInfo> lobbyInfoArrayList) {
        if (mLobbyAdapter == null) {
            mLobbyAdapter = new LobbyAdapter(lobbyInfoArrayList);
            mLobbyListRecyclerView.setAdapter(mLobbyAdapter);
        } else {
            mLobbyAdapter.setLobbyInfoArrayList(lobbyInfoArrayList);
            mLobbyAdapter.notifyDataSetChanged();
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
                mNettyClient.write(new LobbyListUnsubscribeRequest(UserInfo.getAuthToken()), true);
                EnterLobbyResponse enterLobbyResponse = (EnterLobbyResponse) msg.obj;
                mNettyClient.setHandler(null);
                // TODO change api(add lobbyId)
                Intent nextActivity = LobbyActivity.getStartIntent(getApplicationContext(),
                        enterLobbyResponse.getPrivateLobbyInfo(), null);
                startActivity(nextActivity);
                finish();
            } else if (msg.obj instanceof CreateLobbyResponse) {
                Log.d(TAG, "LobbyListActivityHandler: CreateLobbyResponse");
                mNettyClient.write(new LobbyListUnsubscribeRequest(UserInfo.getAuthToken()), true);
                CreateLobbyResponse createLobbyResponse = (CreateLobbyResponse) msg.obj;
                mNettyClient.setHandler(null);
                Intent intent = LobbyActivity.getStartIntent(getApplicationContext(), new PrivateLobbyInfo(),
                        createLobbyResponse.getLobbyId());
                startActivity(intent);
            }
        }
    }
}
