package com.tian.nettystudy.netty.shangxuetang.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * Created by tianxiong on 2019/4/27.
 */
public class Server4Protocol {
    private EventLoopGroup group;
    private EventLoopGroup worker;
    private ServerBootstrap bootstrap;

    public Server4Protocol() {
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(group, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);

    }

    public ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(10240,Unpooled.copiedBuffer("$end$".getBytes("UTF-8"))));
                ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                ch.pipeline().addLast(acceptorHandlers);
            }
        });
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    public void release(){
        this.group.shutdownGracefully();
        this.worker.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Server4Protocol server = null;
        try{
            server = new Server4Protocol();
            future = server.doAccept(9999, new Server4ProtocolHandler());
            System.out.println("server started.");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(future != null){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(server != null){
                server.release();
            }
        }
    }
}
