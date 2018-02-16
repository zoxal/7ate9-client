package com.yatty.sevenatenine.client;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yatty.sevenatenine.api.ConnectRequest;
import com.yatty.sevenatenine.api.ConnectResponse;
import com.yatty.sevenatenine.api.GameStartedEvent;
import com.yatty.sevenatenine.api.MoveRejectedResponse;
import com.yatty.sevenatenine.api.MoveRequest;
import com.yatty.sevenatenine.api.NewStateEvent;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.ByteToMessageCodec;

public class NettyClient {
    private static final String HOST = "192.168.1.105";
    private static final int PORT = 6667;
    private static final String COMMAND_TYPE_FIELD = "_type";
    private static final String TAG = "TAG";

    private static NettyClient nettyClient;
    private HashMap<String, Class> commands;
    private Channel channel;
    private Handler handler;

    private NettyClient(Handler handler) {
        commands = new HashMap<>();
        commands.put(ConnectRequest.COMMAND_TYPE, ConnectRequest.class);
        commands.put(ConnectResponse.COMMAND_TYPE, ConnectResponse.class);
        commands.put(GameStartedEvent.COMMAND_TYPE, GameStartedEvent.class);
        commands.put(MoveRejectedResponse.COMMAND_TYPE, MoveRejectedResponse.class);
        commands.put(MoveRequest.COMMAND_TYPE, MoveRequest.class);
        commands.put(NewStateEvent.COMMAND_TYPE, NewStateEvent.class);
        this.handler = handler;
    }

    public static NettyClient getInstance(Handler handler) {
        if (nettyClient == null) {
            nettyClient = new NettyClient(handler);
            try {
                nettyClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nettyClient.setHandler(handler);
        }
        return nettyClient;
    }

    private void run() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class).handler(new PipeLineInitializer());
        SocketAddress socketAddress = new InetSocketAddress(HOST, PORT);
        channel = bootstrap.connect(socketAddress).sync().channel();
    }

    public void write(Object obj) {
        channel.writeAndFlush(obj).addListener(new ChannelFutureListener() {
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
            Message message = new Message();
            message.obj = ConnectResponse.COMMAND_TYPE;
            handler.sendMessage(message);
        }
    }

    public class GsonCodec extends ByteToMessageCodec<Object> {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            Log.d(TAG, "Encoding...");
            ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(out);
            Gson gson = new Gson();
            String json = gson.toJson(msg);
            byteBufOutputStream.writeBytes(json);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            Log.d(TAG, "Decoding...");
            ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
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
            String json = byteBufInputStream.readLine();
            Log.d(TAG, "Get json: " + json);
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            String type = obj.get(COMMAND_TYPE_FIELD).getAsString();
            Log.d(TAG, "Parsec type: " + type);
            Class clazz = commands.get(type);
            out.add(gson.fromJson(json, clazz));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

    private void setHandler(Handler handler) {
        this.handler = handler;
    }
}