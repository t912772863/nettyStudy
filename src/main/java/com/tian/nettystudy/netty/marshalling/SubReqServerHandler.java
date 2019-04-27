package com.tian.nettystudy.netty.marshalling;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        MsgObject msgObject = (MsgObject)msg;
        if(msgObject.getUserName().startsWith("tianxiong")){
            System.out.println(msgObject.toString());
            ctx.writeAndFlush(msgObject);
        }


    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
