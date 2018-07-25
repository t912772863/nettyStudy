package com.tian.nettystudy.netty.fixlength;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    int counter = 0;
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("This is "+ ++counter +" times receive client:["+ msg+"]");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }



}
