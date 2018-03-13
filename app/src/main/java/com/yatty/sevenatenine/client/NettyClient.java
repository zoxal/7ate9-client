package com.yatty.sevenatenine.client;

import android.os.Handler;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yatty.sevenatenine.api.in_commands.LogInResponse;
import com.yatty.sevenatenine.api.in_commands.GameStartedEvent;
import com.yatty.sevenatenine.api.in_commands.InCommandInterface;
import com.yatty.sevenatenine.api.in_commands.MoveRejectedResponse;
import com.yatty.sevenatenine.api.in_commands.NewStateEvent;
import com.yatty.sevenatenine.api.out_commands.LogInRequest;
import com.yatty.sevenatenine.api.out_commands.MoveRequest;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageCodec;

public class NettyClient {
    private static final String TAG = NettyClient.class.getSimpleName();
    private static final String HOST = "192.168.43.117";
    private static final String TYPE_FIELD = "_type";
    private static final int PORT = 6667;
    private static final String COMMAND_TYPE_FIELD = "_type";
    private static final int SLEEP_TIME_IF_HAS_NO_HANDLER_MS = 5;

    private static NettyClient sNettyClient;
    private HashMap<String, Class> mCommands;
    private Channel mChannel;
    private volatile Handler mHandler;

    private NettyClient() {
        mCommands = new HashMap<>();
        mCommands.put(LogInRequest.COMMAND_TYPE, LogInRequest.class);
        mCommands.put(LogInResponse.COMMAND_TYPE, LogInResponse.class);
        mCommands.put(GameStartedEvent.COMMAND_TYPE, GameStartedEvent.class);
        mCommands.put(MoveRejectedResponse.COMMAND_TYPE, MoveRejectedResponse.class);
        mCommands.put(MoveRequest.COMMAND_TYPE, MoveRequest.class);
        mCommands.put(NewStateEvent.COMMAND_TYPE, NewStateEvent.class);
    }

    public static NettyClient getInstance() {
        if (sNettyClient == null) {
            try {
                sNettyClient = new NettyClient();
                sNettyClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sNettyClient;
    }

    private void run() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class).handler(new PipeLineInitializer());
        SocketAddress socketAddress = new InetSocketAddress(HOST, PORT);
        mChannel = bootstrap.connect(socketAddress).sync().channel();
    }

    public void write(Object obj) {
        mChannel.writeAndFlush(obj).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Log.d(TAG, "Message sent");
                } else {
                    System.out.println("Fail");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    private class PipeLineInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ch.pipeline().addFirst(new GsonCodec());
            ch.pipeline().addLast(new LogicHandler());
        }
    }

    private class LogicHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
            Log.d(TAG, "Got class: " + obj.getClass());
            InCommandInterface command = (InCommandInterface) obj;
            // Test this while!!!
            while (mHandler == null) {
                Log.d(TAG, "SLEEP_TIME_IF_HAS_NO_HANDLER_MS");
                Thread.sleep(SLEEP_TIME_IF_HAS_NO_HANDLER_MS);
            }
            command.doLogic(mHandler);
        }
    }

    public class GsonCodec extends MessageToMessageCodec<DatagramPacket, Object> {

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
            out.add(Unpooled.wrappedBuffer(json.getBytes()));
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
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
            String json = msg.content().toString(Charset.defaultCharset());
            Log.d(TAG, "Get json: " + json);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            String type = obj.get(COMMAND_TYPE_FIELD).getAsString();
            Log.d(TAG, "Parsed type: " + type);
            Class clazz = mCommands.get(type);
            out.add(gson.fromJson(json, clazz));
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}