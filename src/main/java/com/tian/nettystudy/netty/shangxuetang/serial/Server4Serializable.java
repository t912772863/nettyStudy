package com.tian.nettystudy.netty.shangxuetang.serial;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by tianxiong on 2019/4/29.
 */
public class Server4Serializable {
    private EventLoopGroup acceptorGroup;
    private EventLoopGroup clientGroup;
    private ServerBootstrap bootstrap;
    public Server4Serializable(){
        init();
    }

    private void init(){
        acceptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(acceptorGroup, clientGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16*1024);
        bootstrap.option(ChannelOption.SO_RCVBUF, 16*1024);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    public ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<ServerChannel>() {
            @Override
            protected void initChannel(ServerChannel ch) throws Exception {
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingDecoder());
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingEncoder());
                ch.pipeline().addLast(acceptorHandlers);
            }
        });
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Server4Serializable server = null;
        try{
            server = new Server4Serializable();
            future = server.doAccept(9999, new Server4SerializableHandler());
            System.out.println("server started.");
            future.channel().closeFuture().sync();
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
