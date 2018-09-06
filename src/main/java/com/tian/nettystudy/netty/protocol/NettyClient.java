package com.tian.nettystudy.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 *
 * 主要用于初始化系统资源, 根据配置信息发起连接
 *
 * Created by Administrator on 2018/7/26 0026.
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host){
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /*
                            NettyMessageDecoder用于消息解码, 为了防止单条消息过大导致内存溢出或者畸形码流导致解码错位
                            引起内存分配失败, 我们对消息的最大长度进行了限制
                             */
                            pipeline.addLast(new NettyMessageDecoder(1024*1024, 4, 4));
                            // 消息编码器
                            pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
                            // 超时
                            pipeline.addLast("ReadTimeOutHandler", new ReadTimeoutHandler(50));
                            // 登录
                            pipeline.addLast("LoginAuthReqHandler", new LoginAuthReqHandler());
                            // 心跳
                            pipeline.addLast("HeartBeatReqHandler", new HeartBeatReqHandler());
                        }
                    });
            // 发起异常连接操作
            ChannelFuture future = b.connect(new InetSocketAddress(host, port), new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 所有资源释放完成之后, 清空资源, 再次发起重连操作
            executor.execute(new Runnable() {
                public void run() {
                    try{
                        TimeUnit.SECONDS.sleep(5);
                        connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

        }


    }

    public static void main(String[] args) {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }
}
