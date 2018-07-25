package com.tian.nettystudy.netty.delimiterdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 实现的功能就是接收消息后, 打印出来, 再把原始消息回写给客户端
 * 示例中多个消息中以$_分隔.
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
                    // 先把指定的消息间隔符解析出缓冲字节对象
                    ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                    // 把上面的对象添加到间隔符拦截对象链中, 1024表示, 当读到1024个长度还没有遇到指定的$_符号时,拋出异常
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
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
