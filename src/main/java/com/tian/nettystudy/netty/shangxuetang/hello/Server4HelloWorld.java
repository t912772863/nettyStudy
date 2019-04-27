package com.tian.nettystudy.netty.shangxuetang.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class Server4HelloWorld {
    /**
     * 监听线程组, 监听客户端请求
     */
    private EventLoopGroup acciptorGroup;
    /**
     * 处理客户端相关操作线程组, 负责处理与客户端的数据通讯
     */
    private EventLoopGroup clientGroup;
    /**
     * 服务启动相关配置信息
     */
    private ServerBootstrap bootstrap;
    private Server4HelloWorld(){
        init();
    }

    private void init() {
        // 初始化线程组, 如果不传参数,默认为CPU核心数
        acciptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        // 初始化服务端配置
        bootstrap = new ServerBootstrap();
        // 绑定线程组,
        bootstrap.group(acciptorGroup, clientGroup);
        // 设定通讯模式为NIO, 同步非阻塞
        bootstrap.channel(NioServerSocketChannel.class);
        // 设定缓冲区大小,单位字节
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // SO_SNDBUF发送缓冲区, SO_RCVBUF接收缓冲区, SO_KEEPALIVE开启心跳监测(保证连接有效)
        bootstrap.option(ChannelOption.SO_SNDBUF, 16*1024)
                .option(ChannelOption.SO_RCVBUF, 16*1024)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    /**
     * 处理监听逻辑
     * @param port 端口号
     * @param acceptorHandlers 处理器, 如何处理客户端请求的
     * @return
     * @throws InterruptedException
     */
    public ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException {
        /*
        childHandler是服务端BootStrap独有的方法, 是用于提供处理对象的. 可以一次性增加若干个处理逻辑, 是类似责任链处理模式.
        增加A, B两个处理器, 在处理客户端请求数据的时候,根据A->B依次处理.

        ChannelInitializer用于提供处理器的一个模型对象, 其中定义了一个方法,initChannel方法. 该方法是用于初始化处理逻辑责任
        链的. 可以保证服务端的BootStrap只初始化一次, 尽量提供处理逻辑的重用, 可以避免反复的创建处理器对象,节约开销.
         */
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(acceptorHandlers);
            }
        });
        /*
        bind是用于绑定监听端口的, ServerBootStrap可以绑定多个监听端口,只需要多次调用bind方法就可以了.
        sync -- 开始启动监听逻辑, 返回一个ChannelFuture, 返回结果代表监听成功后的一个未来结果.可以使用ChannelFuture实现
        后续服务器与客户端的交互.
         */
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    /**
     * shutdownGracefully 是一个安全关闭的方法,可以保证不放弃任何一个已经接收的客户端请求.
     */
    public void release(){
        this.acciptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Server4HelloWorld server = null;
        try{
            server = new Server4HelloWorld();
            future = server.doAccept(9999, new Server4HelloWorldHandler());
            System.out.println("server started.");
            // 关闭连接的
            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(future != null){
                try{
                    future.channel().closeFuture().sync();
                }catch (Exception e){

                }
            }
            if(server != null){
                server.release();
            }
        }


    }

}
