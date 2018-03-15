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

import com.yatty.sevenatenine.api.commands_with_data.PublicLobbyInfo;
import com.yatty.sevenatenine.api.in_commands.CreateLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.EnterLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.LobbyListUpdatedNotification;
import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.EnterLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.LobbySubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyUnsubscribeRequest;

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
        retrieveInfoFromIntent();
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
        LobbySubscribeRequest lobbySubscribeRequest = new LobbySubscribeRequest(UserInfo.getAuthToken());
        mNettyClient.write(lobbySubscribeRequest, true);
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
            mNettyClient.write(createLobbyRequest, false);
        }
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LobbyListActivity.class);
        //intent.putExtra(EXTRA_AUTH_TOKEN, authToken);
        return intent;
    }

    public static Intent getIntentWithData(CreateLobbyRequest createLobbyRequest) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CREATE_LOBBY_REQUEST, createLobbyRequest);
        return intent;
    }

    private void retrieveInfoFromIntent() {
        Intent intent = getIntent();
        //mAuthToken = intent.getStringExtra(EXTRA_AUTH_TOKEN);
    }

    private class LobbyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mLobbyNameTextView;
        private PublicLobbyInfo mPublicLobbyInfo;

        public LobbyHolder(View itemView) {
            super(itemView);
            mLobbyNameTextView = itemView.findViewById(R.id.tv_lobby_name);
        }

        public void bindLobby(PublicLobbyInfo publicLobbyInfo) {
            Log.d(TAG, "bindLobby: " + publicLobbyInfo.getLobbyName());
            mPublicLobbyInfo = publicLobbyInfo;
            mLobbyNameTextView.setText(mPublicLobbyInfo.getLobbyName());
        }

        @Override
        public void onClick(View v) {
            EnterLobbyRequest enterLobbyRequest = new EnterLobbyRequest(mPublicLobbyInfo, UserInfo.getAuthToken());
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
                Log.d(TAG, "Binding task: " + lobbyInfo.getLobbyName());
            }
            holder.bindLobby(lobbyInfo);
        }

        public void setLobbyInfoArrayList(ArrayList<PublicLobbyInfo> lobbyInfoArrayList) {
            mLobbyInfoArrayList = lobbyInfoArrayList;
        }

        @Override
        public int getItemCount() {
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
                LobbyListUpdatedNotification lobbyListUpdatedNotification = (LobbyListUpdatedNotification) msg.obj;
                updateLobbyList(lobbyListUpdatedNotification.getPublicLobbyInfoList());
            } else if (msg.obj instanceof EnterLobbyResponse) {
                EnterLobbyResponse enterLobbyResponse = (EnterLobbyResponse) msg.obj;
                Intent nextActivity = LobbyActivity.getStartIntent(getApplicationContext(),
                        enterLobbyResponse.getPrivateLobbyInfo());
                startActivity(nextActivity);
                finish();
            } else if (msg.obj instanceof CreateLobbyResponse) {
                Log.d(TAG, "LobbyListActivityHandler catch CreateLobbyResponse");
                CreateLobbyResponse createLobbyResponse = (CreateLobbyResponse) msg.obj;
                mNettyClient.write(new LobbyUnsubscribeRequest(UserInfo.getAuthToken()), false);
                mNettyClient.setHandler(null);
                Intent intent = LobbyActivity.getStartIntent(getApplicationContext());
                startActivity(intent);
            }
        }
    }
}