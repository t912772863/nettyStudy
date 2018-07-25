package com.tian.nettystudy.netty.timedemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 通过netty框架, 实现TimeServer以及TimeClient的功能.看看对比jdk自带的工具类, netty框架能简化多少代码,
 * Created by Administrator on 2018/7/23 0023.
 */
public class TimeServer {
    /**
     *
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception{
        // 配置服务湍的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
            // 绑定端口, 同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 优雅关闭, 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
       new TimeServer().bind(port);
    }
}
