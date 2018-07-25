package com.tian.nettystudy.netty.timedemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 支持TCP粘包的版本
 * Created by Administrator on 2018/7/24 0024.
 */
public class TimeServer2 {
    public void bind(int port){
        // 配置线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler2());
            // 绑定端口, 同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    private class ChildChannelHandler2 extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) throws Exception {
            /*
            LineBasedFrameDecoder的工作原理是它依次遍历ByteBuf中的可读字节,判断是否有\n或者\r\n
            如果有, 就以此位置为结束位置, 从可读索引到结束位置区间的字节就组成了一行. 它是以换行符为
            结束标志的解码器. 支持带结束符,或者不带结束符两种解码方式, 同时支持配置单行的最大长度. 如果
            连续读到最大长度后还没有换行符, 就会拋出异常. 同时忽略掉之前读到的异常码流
             */
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            /*
            StringDecoder的功能非常简单, 就是将接收到的对象转成字符串, 然后继续调用后面的Handler,
            LineBasedFrameDecoder + StringDecoder组合主是按行切换文本的解码器, 它被设计用来扶持TCP
            的粘包的拆包.
             */
            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new TimeServerHandler2());

        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new TimeServer2().bind(port);

    }
}
