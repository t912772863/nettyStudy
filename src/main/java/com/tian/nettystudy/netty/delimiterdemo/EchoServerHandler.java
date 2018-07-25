package com.tian.nettystudy.netty.delimiterdemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    int counter = 0;
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String body = (String)msg;
        System.out.println("This is "+ ++counter +" times receive client:["+ body+"]");
        body = body+"$_";
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();

    }



}
