package com.tian.nettystudy.netty.delimiterdemo;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int counter;
    public static final String ECHO_REQ= "Hi, TianXiong, welcome to netty $_";

    public void channelActive(ChannelHandlerContext ctx){
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("This is "+ ++counter +" times receive server: ["+ msg+"]");
    }

    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }




}
