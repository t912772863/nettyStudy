package com.tian.nettystudy.netty.timedemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class TimeServerHandler2 extends ChannelHandlerAdapter {
    private int counter;
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String body = (String)msg;
        System.out.println("The time server receive order: "+body+" ; the counter is: "+ ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";
        currentTime = currentTime + "\n";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
    }



}
