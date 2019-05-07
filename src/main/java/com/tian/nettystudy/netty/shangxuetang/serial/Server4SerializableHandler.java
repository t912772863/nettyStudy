package com.tian.nettystudy.netty.shangxuetang.serial;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Created by tianxiong on 2019/5/2.
 */
public class Server4SerializableHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        System.out.println("from client: className - "+msg.getClass().getName()+"; message: "+msg.toString());
        if(msg instanceof RequestMessage){
            RequestMessage request = (RequestMessage)msg;
//            byte[] attachment = GzipUtils.unzip(request.getAttachment());
//            System.out.println(new String(attachment));
        }
        ResponseMessage response = new ResponseMessage(0L, "test response");
        ctx.writeAndFlush(response);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("server exceptionCaught method run...");
        cause.printStackTrace();
        ctx.close();
    }

}
