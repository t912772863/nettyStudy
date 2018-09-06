package com.tian.nettystudy.netty.httpsocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 基于netty实现webSocket协议, 服务端代码
 * Created by Administrator on 2018/7/26 0026.
 */
public class WebSocketServer {
    public void run(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /*
                            HttpServerCodec用于将请求和应答消息编码或者解码为http消息
                             */
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            /*
                            HttpObjectAggregator的目的是将HTTP消息的多个部分组合成一条完整的HTTP消息
                             */
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            /*
                            ChunkedWriteHandler用来向客户端发送的html5文件, 它主要用于支持服务端和浏览器进行webSocket通信
                             */
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            /*
                            自定义的webSocket拦截器, 实现逻辑
                             */
                            pipeline.addLast("handler", new WebSocketServerHandler());
                        }
                    });
            Channel ch = b.bind(port).sync().channel();
            System.out.println("Web socket server start at port: "+port+". Open your browser and navigate to 'ws://localhost:"+port+"/'");

            ch.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length >0){
            port = Integer.valueOf(args[0]);
        }
        new WebSocketServer().run(port);

    }





}
