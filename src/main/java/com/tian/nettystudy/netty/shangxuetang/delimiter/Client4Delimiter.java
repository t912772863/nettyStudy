package com.tian.nettystudy.netty.shangxuetang.delimiter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class Client4Delimiter {
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public Client4Delimiter(){
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);

    }

    public ChannelFuture doRequest(String host, int port) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelHandler[] handlers = new ChannelHandler[3];
                ByteBuf delimiter = Unpooled.copiedBuffer("$E$".getBytes());
                handlers[0] = new DelimiterBasedFrameDecoder(1024,delimiter);
                handlers[1] = new StringDecoder(Charset.forName("UTF-8"));
                handlers[2] = new Client4DelimiterHandler();
                ch.pipeline().addLast(handlers);

            }
        });
        ChannelFuture future = this.bootstrap.connect(host, port).sync();
        return future;
    }

    public void release(){
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        Client4Delimiter client = null;
        ChannelFuture future = null;
        try{
            client = new Client4Delimiter();
            future = client.doRequest("127.0.0.1", 9999);
            Scanner s = null;
            while (true){
                s = new Scanner(System.in);
                System.out.println("enter message send to server>");
                String line = s.nextLine();
                future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
                TimeUnit.SECONDS.sleep(1);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(future != null){
                try{
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(client != null){
                client.release();
            }
        }

    }

}
