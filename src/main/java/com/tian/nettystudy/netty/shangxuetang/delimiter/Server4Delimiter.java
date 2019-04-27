package com.tian.nettystudy.netty.shangxuetang.delimiter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
public class Server4Delimiter {
    private EventLoopGroup group;
    private EventLoopGroup worker;
    private ServerBootstrap bootstrap;

    public Server4Delimiter(){
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        bootstrap.group(group, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16*1024)
                .option(ChannelOption.SO_RCVBUF, 16*1024)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public ChannelFuture doAccept(int port) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 数据分割符, 一定是一个ByteBuf类型的对象
                ByteBuf delimiter = Unpooled.copiedBuffer("$E$".getBytes());
                ChannelHandler[] acceptorHandlers = new ChannelHandler[3];
                // 处理固定结束标记符号的handler, 这个handler没有@Sharable注解修饰, 必须每次初始化通道时合建一个新对象
                // 使用特殊符号处理粘包问题, 也要定义数据包最大长度, 因为netty建议数据包有最大长度
                acceptorHandlers[0] = new DelimiterBasedFrameDecoder(1024,delimiter);
                // 字符串解码器
                acceptorHandlers[1] = new StringDecoder(Charset.forName("UTF-8"));
                acceptorHandlers[2] = new Server4DelimiterHandler();
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
        Server4Delimiter server = null;
        try{
            server = new Server4Delimiter();
            future = server.doAccept(9999);
            System.out.println("server started.");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(server != null){
                server.release();
            }
        }

    }
}
