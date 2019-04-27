package com.tian.nettystudy.netty.shangxuetang.fixlength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;


/**
 * Created by tianxiong on 2019/4/22.
 */
public class Server4FixedLength {
    private EventLoopGroup acceptorGroup;
    private EventLoopGroup clientGroup;
    private ServerBootstrap bootstrap;

    public Server4FixedLength(){
        init();
    }

    private void init() {
        acceptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(acceptorGroup, clientGroup);
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
                ChannelHandler[] acceptorHandlers = new ChannelHandler[3];
                // 定长handler, 通过构造参数设置消息长度, 发送的消息长度不足可以使用空字符填充. 单位是字节
                acceptorHandlers[0] =new FixedLengthFrameDecoder(3);
                // 字符串解析器handler, 会自动处理channelRead方法的msg参数, 将ByteBuf类型的数据转换为字符串类型
                acceptorHandlers[1] = new StringDecoder(Charset.forName("UTF-8"));
                acceptorHandlers[2] = new Server4FixedLengthHandler();
                ch.pipeline().addLast(acceptorHandlers);

            }
        });
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Server4FixedLength server = null;
        try{
            server = new Server4FixedLength();
            future = server.doAccept(9999);
            System.out.println("server started.");
            future.channel().closeFuture().sync();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(future != null){
                try {
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(server != null){
                server.release();
            }
        }
    }

}
