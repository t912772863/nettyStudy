package com.tian.nettystudy.netty.shangxuetang.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 因为客户端是请求的发起者, 不需要监听, 只需要定义一个线程组就可以了.
 *
 * Created by tianxiong on 2019/4/22.
 */
public class Client4HelloWorld {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    public Client4HelloWorld(){
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 绑定线程组
        bootstrap.group(group);
        // 设定通讯模式为NIO
        bootstrap.channel(NioSocketChannel.class);
    }

    public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws InterruptedException {
        /*
        客户端的BootStrap没有childHandler方法, 只有handler方法.
        方法含义等同ServerBootStrap中的childHandler方法.
        在客户端必须绑定处理器, 也就是必须调用handler方法, 也就是必须调用childHandler方法
         */
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handlers);
            }
        });
        // 客户端是connect连接方法,服务端这里是bind方法
        ChannelFuture future = this.bootstrap.connect(host, port).sync();
        return future;
    }

    public void release(){
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        Client4HelloWorld client = null;
        ChannelFuture future = null;
        try{
            client = new Client4HelloWorld();
            future = client.doRequest("127.0.0.1", 9999, new Client4HelloWorldHandler());
            Scanner s = null;
            while (true){
                System.out.println("enter message send to server (enter 'exit' for close client.)");
                s = new Scanner(System.in);
                String line = s.nextLine();
                System.out.println("输入内容: "+line);
                if("exit".equals(line)){
                    /*
                    addListener 增加监听, 当某条件满足的时候, 触发监听器.
                    ChannelFutureListener.CLOSE 关闭监听器, 代表执行返回后关闭连接.
                    Unpooled是一个工具类, 可以转换ByteBuf
                     */
                    future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")))
                    .addListener(ChannelFutureListener.CLOSE);
                    break;
                }
                future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
                TimeUnit.SECONDS.sleep(1);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(future != null){
                try {
                    future.channel().closeFuture().sync();
                }catch (Exception e){

                }
            }
            if(client != null){
                client.release();
            }
        }

    }

}
