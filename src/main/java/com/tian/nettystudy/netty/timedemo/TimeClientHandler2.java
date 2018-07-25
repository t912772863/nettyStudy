package com.tian.nettystudy.netty.timedemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class TimeClientHandler2 extends ChannelHandlerAdapter {
    private int counter;
    private byte[] req;
    public TimeClientHandler2(){
        req = ("QUERY TIME ORDER"+"\n").getBytes();
    }

    public void channelActive(ChannelHandlerContext ctx){
        ByteBuf message = null;
        for (int i = 0; i <100 ; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);

        }

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String body = (String)msg;
        System.out.println("Now is : "+body+" ;the counter is : "+ ++counter);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();

    }

}
