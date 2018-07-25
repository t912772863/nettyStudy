package com.tian.nettystudy.serialize.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoServerHandler2 extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
//        UserInfo body = (UserInfo)msg;
        System.out.println("The server receive message: "+msg.toString());
        ctx.writeAndFlush(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();

    }



}
