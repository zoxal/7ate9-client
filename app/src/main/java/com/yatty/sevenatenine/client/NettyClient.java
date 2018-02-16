package com.yatty.sevenatenine.client;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yatty.sevenatenine.api.ConnectRequest;
import com.yatty.sevenatenine.api.ConnectResponse;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
    private static final String HOST = "";
    private static final int PORT = 8080;
    private static final String IGNORED_FIELD = "_type";
    private static final String TAG = "TAG";
    private Channel channel;

    public static void main(String[] args) throws Exception {
        new NettyClient().run();
    }

    private void run() throws Exception {
        EventLoopGroup elg = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(elg).channel(NioDatagramChannel.class).handler(new PipeLineInitializer());
        SocketAddress sa = new InetSocketAddress(HOST, PORT);
        channel = b.connect(sa).channel();
    }

    public void write(Object obj) {
        channel.writeAndFlush(obj).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Message sent");
                } else {
                    future.cause().printStackTrace();
                }
            }
        });
    }

    private static class PipeLineInitializer extends ChannelInitializer<NioDatagramChannel> {
        @Override
        protected void initChannel(NioDatagramChannel ch) throws Exception {
            ch.pipeline().addFirst(new BasicJacksonCodec());
            ch.pipeline().addLast(new LogicHandler());
        }
    }

    private static class LogicHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
            System.out.println("Got class: " + obj.getClass());
        }
    }

    public static class BasicJacksonCodec<T> extends ByteToMessageCodec<T> {


        @Override
        protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
            System.out.println("Encoding...");
            ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(out);
            Gson gson = new Gson();
            String value = gson.toJson(msg);
            byteBufOutputStream.writeBytes(value);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println("Decoding...");
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
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            String type = obj.get(IGNORED_FIELD).getAsString();
            Log.d(TAG, "Parsec type: " + type);
            Class clazz;
            switch (type) {
                case "ConnectRequest":
                    clazz = ConnectRequest.class;
                    break;
                case "ConnectResponse":
                    clazz = ConnectResponse.class;
                    break;
                default:
                    Log.d(TAG, "ADD CLASS TO SWITCH CASE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    clazz = ConnectRequest.class;
                    break;
            }
            out.add(gson.fromJson(json, clazz));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

}