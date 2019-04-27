package com.tian.nettystudy.netty.shangxuetang.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by tianxiong on 2019/4/27.
 */
public class Client4ProtocolHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String message = msg.toString();
        System.out.println("client receive message: "+message);
        message = Server4ProtocolHandler.ProtocolParser.parse(message);
        System.out.println("after parse: "+message);
        ReferenceCountUtil.release(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("client exceptionCaught method is run...");
        cause.printStackTrace();
        ctx.close();
    }
}
