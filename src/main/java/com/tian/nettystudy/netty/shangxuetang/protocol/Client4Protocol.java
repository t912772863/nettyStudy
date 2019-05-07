package com.tian.nettystudy.netty.shangxuetang.protocol;

import io.netty.bootstrap.Bootstrap;
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
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.nio.charset.Charset;

/**
 *
 * Created by tianxiong on 2019/4/27.
 */
public class Client4Protocol {
    private EventLoopGroup workers;
    private Bootstrap bootstrap;
    private ChannelFuture future;

    public Client4Protocol() {
        init();
    }

    private void init() {
        workers = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workers);
        bootstrap.channel(NioSocketChannel.class);
    }

    public ChannelFuture doRequest(int port, final ChannelHandler... channelHandlers) throws InterruptedException {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                /*
                第一过处理器是自定义的一个特殊字符, 用来解析粘包, 拆包问题.
                保证后面处理器拿到的是按照自定义协议格式的消息, 然后按定义的格式解析消息
                 */
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(10240,Unpooled.copiedBuffer("$end$".getBytes("UTF-8"))));
                ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                // 3秒不写操作,自动断开
                ch.pipeline().addLast(new WriteTimeoutHandler(3));
                ch.pipeline().addLast(channelHandlers);
            }
        });
        future = bootstrap.connect("127.0.0.1", port).sync();
        return future;
    }

    public void release(){
        this.workers.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Client4Protocol client = null;
        try{
            client = new Client4Protocol();
            future = client.doRequest(9999, new Client4ProtocolHandler());
            // 不休眠, 快速循环写出, 查看粘包和协议问题是否解决.
            for (int i = 0; i < 100; i++) {
                future.channel().writeAndFlush(Unpooled.copiedBuffer((Server4ProtocolHandler.ProtocolParser.transferTo(""+i)+"$end$").getBytes("UTF-8")));
//                Thread.sleep(2000);
            }
            // 休眠超过设置超时时长, 则需要重新与服务端连接
            Thread.sleep(5000);
            future = client.getChannelFuture("127.0.0.1",9999);
            future.channel().writeAndFlush(Unpooled.copiedBuffer((Server4ProtocolHandler.ProtocolParser.transferTo(""+"test")+"$end$").getBytes("UTF-8")));

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
//            if(client != null){
//                client.release();
//            }
        }

    }

    private ChannelFuture getChannelFuture(String host, int port) throws InterruptedException {
        if(future == null){
            future = this.bootstrap.connect(host, port).sync();
        }
        if(!future.channel().isActive()){
            future = this.bootstrap.connect(host, port).sync();
        }

        return future;
    }
}
