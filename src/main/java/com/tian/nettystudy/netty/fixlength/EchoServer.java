package com.tian.nettystudy.netty.fixlength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 定长解析
 *
 * DelimiterBasedFrameDecoder 服务端开发.
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoServer {
    public void bind(int port){
        // 配置服务端NIO线程组
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workergroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossgroup, workergroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 指定FixedLengthFrameDecoder解码器, 无论一次接收到多少数据报, 它都会按照构造函数中指定的长度进行
                    // 解码, 如果是半包消息, 会先缓存消息, 等待下个包到达后, 进行拼包, 直到读到一个完整的包为止
                   ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
                    ch.pipeline().addLast(new StringDecoder());
                    // 最后添加自己定义的拦截器对象
                    ch.pipeline().addLast(new EchoServerHandler());
                }
            });
            // 绑定端口, 同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossgroup.shutdownGracefully();
            workergroup.shutdownGracefully();

        }


    }

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new EchoServer().bind(port);

    }

}
