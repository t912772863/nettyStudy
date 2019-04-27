package com.tian.nettystudy.netty.shangxuetang.delimiter;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class Client4DelimiterHandler extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try{
            String message = msg.toString();
            System.out.println("from server: "+message);
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("client exceptionCaught method run...");
        ctx.close();
    }
}
