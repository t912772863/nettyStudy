package com.tian.nettystudy.netty.shangxuetang.serial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianxiong on 2019/5/6.
 */
public class Client4Serializable {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    public Client4Serializable(){
        init();
    }

    private void init(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
    }

    public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingDecoder());
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingEncoder());
                ch.pipeline().addLast(handlers);
            }
        });
        ChannelFuture future = this.bootstrap.connect(host, port).sync();
        return future;
    }

    public void release(){
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        Client4Serializable client = null;
        ChannelFuture future = null;
        try{
            client = new Client4Serializable();
            future = client.doRequest("127.0.0.1", 9999, new Client4SerializableHandler());
            String  attachment = "test attachment";
            byte[] attBuf = attachment.getBytes("UTF-8");
//            attBuf = GzipUtils.zip(attBuf);
            RequestMessage msg =new RequestMessage(new Random().nextLong(), "test",new byte[0] );
            future.channel().writeAndFlush(msg);
            TimeUnit.SECONDS.sleep(1);
            future.channel().closeFuture().sync();
            future.addListener(ChannelFutureListener.CLOSE);

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
            if(client != null){
                client.release();
            }
        }

    }
}
