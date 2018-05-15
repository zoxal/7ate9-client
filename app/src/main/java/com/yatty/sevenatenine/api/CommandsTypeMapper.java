package com.yatty.sevenatenine.api;

import com.yatty.sevenatenine.api.in_commands.CreateLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.EnterLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.ErrorResponse;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.in_commands.LobbyListUpdatedNotification;
import com.yatty.sevenatenine.api.in_commands.LobbyStateChangedNotification;
import com.yatty.sevenatenine.api.in_commands.LogInResponse;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateNotification;
import com.yatty.sevenatenine.api.out_commands.CreateLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.EnterLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.KeepAliveRequest;
import com.yatty.sevenatenine.api.out_commands.LeaveLobbyRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListSubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LobbyListUnsubscribeRequest;
import com.yatty.sevenatenine.api.out_commands.LogInRequest;
import com.yatty.sevenatenine.api.out_commands.LogOutRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;

import java.util.HashMap;
import java.util.Map;

public class CommandsTypeMapper {
    public static final String TYPE_FIELD = "_type";
    public static final String COMMAND_TYPE_FIELD = "_type";
    private static Map<String, Class<?>> mCommands = new HashMap<>(32);

    static {
        mCommands = new HashMap<>();
        // in commands
        mCommands.put(CreateLobbyResponse.class.getSimpleName(), CreateLobbyResponse.class);
        mCommands.put(EnterLobbyResponse.class.getSimpleName(), EnterLobbyResponse.class);
        mCommands.put(LobbyStateChangedNotification.class.getSimpleName(), LobbyStateChangedNotification.class);
        mCommands.put(ErrorResponse.class.getSimpleName(), ErrorResponse.class);
        mCommands.put(LobbyListUpdatedNotification.class.getSimpleName(), LobbyListUpdatedNotification.class);
        mCommands.put(LogInResponse.class.getSimpleName(), LogInResponse.class);
        mCommands.put(GameStartedNotification.class.getSimpleName(), GameStartedNotification.class);
        mCommands.put(MoveRejectedResponse.class.getSimpleName(), MoveRejectedResponse.class);
        mCommands.put(NewStateNotification.class.getSimpleName(), NewStateNotification.class);

//        // out commands
//        mCommands.put(CreateLobbyRequest.class.getSimpleName(), CreateLobbyRequest.class);
//        mCommands.put(EnterLobbyRequest.class.getSimpleName(), EnterLobbyRequest.class);
//        mCommands.put(KeepAliveRequest.class.getSimpleName(), KeepAliveRequest.class);
//        mCommands.put(LeaveLobbyRequest.class.getSimpleName(), LeaveLobbyRequest.class);
//        mCommands.put(LobbyListSubscribeRequest.class.getSimpleName(), LobbyListSubscribeRequest.class);
//        mCommands.put(LobbyListUnsubscribeRequest.class.getSimpleName(), LobbyListUnsubscribeRequest.class);
//        mCommands.put(LogInRequest.class.getSimpleName(), LogInRequest.class);
//        mCommands.put(LogOutRequest.class.getSimpleName(), LogOutRequest.class);
//        mCommands.put(MoveRequest.class.getSimpleName(), MoveRequest.class);
    }

    public static Class<?> getClass(String typeName) {
        return mCommands.get(typeName);
    }
}
