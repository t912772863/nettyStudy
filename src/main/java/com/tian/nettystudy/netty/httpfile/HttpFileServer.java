package com.tian.nettystudy.netty.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * http文件服务器, 启动类
 *
 * Created by Administrator on 2018/7/25 0025.
 */
public class HttpFileServer {
    private static final String DEFAULT_URL = "/";

    public void run(final int port, final String url){
        // 创建两个服务端线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 服务端协助启动类
            ServerBootstrap b = new ServerBootstrap();
            // 设置父线程组,子线程组, 这两个线程组将来处理所有的事件以及IO操作
            b.group(bossGroup, workerGroup)
                    // 这里传入一个class对象. 将由netty内部创建该类型的实例
                    .channel(NioServerSocketChannel.class)
                    // 设置子处理器, 这些处理器将被用来为Channel通道提供请求
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /*
                            添加http请求消息解码器,  ch.pipeline()返回分配的管道   addLast在管道上添加处理器
                             */
                            ch.pipeline().addLast("httpfile-decoder", new HttpRequestDecoder());
                            /*
                            HttpObjectAggregator的作用是将多个消息转成单一的FullHttpRequest或者FullHttpResponse,
                            原因是前面的http消息解码器在每个http消息中会生成多个消息对象.
                             */
                            ch.pipeline().addLast("httpfile-aggregator", new HttpObjectAggregator(65536));
                            /*
                            Http响应编码器
                             */
                            ch.pipeline().addLast("httpfile-encoder", new HttpResponseEncoder());
                            /*
                            ChunkedWriteHandler作用是支持异步发送大的码流, 但不占用过多的内存, 防止产生内存溢出
                             */
                            ch.pipeline().addLast("httpfile-chunked", new ChunkedWriteHandler());
                            /*
                            HttpFileServerHandler用于业务逻辑处理
                             */
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler());

                        }
                    });

            ChannelFuture future = b.bind("127.0.0.1", port).sync();
            System.out.println("HTTP文件服务器启动, 网址是: "+ "httpfile://127.0.0.1:"+port+url);
            future.channel().closeFuture().sync();

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
        String url = DEFAULT_URL;
        if(args != null && args.length >1){
            url = args[1];
        }
        new HttpFileServer().run(port, url);

    }

}
