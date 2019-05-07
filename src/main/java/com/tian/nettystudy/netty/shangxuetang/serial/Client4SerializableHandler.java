package com.tian.nettystudy.netty.shangxuetang.serial;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by tianxiong on 2019/5/6.
 */
public class Client4SerializableHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("from server: className - "+msg.getClass().getName()+"; message: "+msg.toString());
//        ReferenceCountUtil.release(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("client exceptionCaught method is run...");
        cause.printStackTrace();
        ctx.close();
    }

}
