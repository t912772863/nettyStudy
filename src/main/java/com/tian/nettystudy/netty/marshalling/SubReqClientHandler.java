package com.tian.nettystudy.netty.marshalling;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.jibx.binding.model.ClassHierarchyContext;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    public void channelActive(ChannelHandlerContext ctx){
        for (int i = 0; i <10 ; i++) {
            MsgObject msgObject = new MsgObject();
            msgObject.setUserName("tianxiong"+i);
            ctx.write(msgObject);
        }
        ctx.flush();
    }

    public void channelRead(ClassHierarchyContext ctx , Object msg){
        System.out.println("receive server response: "+ msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
