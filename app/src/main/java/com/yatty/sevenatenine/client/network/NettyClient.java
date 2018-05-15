package com.yatty.sevenatenine.client.network;

import android.os.Handler;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yatty.sevenatenine.api.in_commands.CreateLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.EnterLobbyResponse;
import com.yatty.sevenatenine.api.in_commands.ErrorResponse;
import com.yatty.sevenatenine.api.in_commands.GameStartedNotification;
import com.yatty.sevenatenine.api.in_commands.InCommandInterface;
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
import com.yatty.sevenatenine.client.auth.SessionInfo;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

class NettyClient {
    private static final String TAG = NettyClient.class.getSimpleName();
    private static final String TYPE_FIELD = "_type";
    private static final String COMMAND_TYPE_FIELD = "_type";
    private static final int SLEEP_TIME_IF_HAS_NO_HANDLER_MS = 5;

    private static NettyClient sNettyClient;
    private String mServerIp = "192.168.0.101";
    private int mPort = 39405;
    private HashMap<String, Class> mCommands;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Channel mChannel;
    private volatile Handler mHandler;
    private volatile AtomicBoolean keepAlive = new AtomicBoolean(false);
    private Semaphore mConnectedSemaphore = new Semaphore(1);

    private NettyClient() {
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

        // out commands
        mCommands.put(CreateLobbyRequest.class.getSimpleName(), CreateLobbyRequest.class);
        mCommands.put(EnterLobbyRequest.class.getSimpleName(), EnterLobbyRequest.class);
        mCommands.put(KeepAliveRequest.class.getSimpleName(), KeepAliveRequest.class);
        mCommands.put(LeaveLobbyRequest.class.getSimpleName(), LeaveLobbyRequest.class);
        mCommands.put(LobbyListSubscribeRequest.class.getSimpleName(), LobbyListSubscribeRequest.class);
        mCommands.put(LobbyListUnsubscribeRequest.class.getSimpleName(), LobbyListUnsubscribeRequest.class);
        mCommands.put(LogInRequest.class.getSimpleName(), LogInRequest.class);
        mCommands.put(LogOutRequest.class.getSimpleName(), LogOutRequest.class);
        mCommands.put(MoveRequest.class.getSimpleName(), MoveRequest.class);

    }

    public static NettyClient getInstance() {
        if (sNettyClient == null) {
            try {
                sNettyClient = new NettyClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sNettyClient;
    }

    public void reconnect(final String serverIp) {
        eventLoopGroup.shutdownGracefully().addListener(future -> {
            Log.d(TAG, "Client has been shut down");
            setServerIp(serverIp);
            connect();
        });
    }

    public void connect() {
        try {
            mConnectedSemaphore.acquire();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(mServerIp, mPort))
                    .handler(new PipeLineInitializer());
            mChannel = bootstrap.connect().addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    mConnectedSemaphore.release();
                    Log.d(TAG, "Connected to server");
                } else {
                    future.cause().printStackTrace();
                    Log.d(TAG, "Failed to connect to server", future.cause());
                    throw new RuntimeException("Failed to connect to server", future.cause());
                }
                mConnectedSemaphore.release();
            }).sync().channel();
            mChannel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> e) throws Exception {
                    if (keepAlive.get()) {
                        Log.d(TAG, "Connection closed, reopening...");
                        NettyClient.this.connect();
                        if (SessionInfo.getAuthToken() != null) {
                            mChannel.writeAndFlush(new KeepAliveRequest(SessionInfo.getAuthToken()));
                        }
                    } else {
                        Log.d(TAG, "Connection closed, do not reopen");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to connect to server", e);
        }
    }

    public void sendMessage(Object obj, boolean keepAlive) {
        Log.d(TAG, "sendMessage started");
        Log.d("NetworkService", "NettyClient called");
        try {
            Log.d(TAG, "Sending message, semaphore: " + mConnectedSemaphore.availablePermits());
            mConnectedSemaphore.acquire();
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to acquire netty sending semaphores", e);
        }
        Log.d(TAG, "Got semaphore");
        this.keepAlive.set(keepAlive);
        if (mChannel == null) {
            Log.d(TAG, "Channel is null!!!");
            return;
        }
        if (!mChannel.isOpen()) {
            connect();
        }
        mChannel.writeAndFlush(obj).addListener((ChannelFutureListener) future -> {
            mConnectedSemaphore.release();
            if (future.isSuccess()) {
                Log.d(TAG, "Message sent, semaphore: " + mConnectedSemaphore.availablePermits());
            } else {
                Log.e(TAG, "Failed to send message", future.cause());
            }
        });
    }

    private class PipeLineInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addFirst(new JsonObjectDecoder());
            ch.pipeline().addLast(new GsonDecoder());
            ch.pipeline().addLast(new LogicHandler());
            ch.pipeline().addLast(new GsonEncoder());
        }
    }

    public String getServerIp() {
        return mServerIp;
    }

    public void setServerIp(String serverIp) {
        mServerIp = serverIp;
    }

    private class LogicHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
            if (!keepAlive.get()) {
                ctx.channel().close();
            }
            Log.d(TAG, "Got class: " + obj.getClass());
            InCommandInterface command = (InCommandInterface) obj;
            while (mHandler == null) {
                Log.d(TAG, "SLEEP_TIME_IF_HAS_NO_HANDLER_MS");
                Thread.sleep(SLEEP_TIME_IF_HAS_NO_HANDLER_MS);
            }
            command.doLogic(mHandler);
        }
    }

    public class GsonDecoder extends MessageToMessageDecoder<ByteBuf> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            Log.d(TAG, "Decoding...");
            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getName().equals("_type");
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }).create();
            String json = msg.readBytes(msg.readableBytes()).toString(Charset.defaultCharset());
            Log.d(TAG, "Get json: " + json);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            String type = obj.get(COMMAND_TYPE_FIELD).getAsString();
            Log.d(TAG, "Parsed type: " + type);
            Class clazz = mCommands.get(type);
            out.add(gson.fromJson(json, clazz));
        }
    }

    public class GsonEncoder extends MessageToMessageEncoder<Object> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            Log.d(TAG, "Encoding...");
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(msg);
            jsonElement.getAsJsonObject().addProperty(TYPE_FIELD, msg.getClass().getSimpleName());
            String json = gson.toJson(jsonElement);
            Log.d(TAG, "Sending: " + json);
            out.add(Unpooled.wrappedBuffer(json.getBytes()));
        }
    }


    public int getPort() {
        return mPort;
    }

    public void setPort(int mPort) {
        mPort = mPort;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
