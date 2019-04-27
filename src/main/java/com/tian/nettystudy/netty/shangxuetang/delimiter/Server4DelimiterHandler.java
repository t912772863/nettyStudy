package com.tian.nettystudy.netty.shangxuetang.delimiter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * Created by tianxiong on 2019/4/27.
 */
public class Server4DelimiterHandler extends ChannelHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        String message = msg.toString();
        System.out.println("from client: "+message);
        String line = "server message $E$ test delimiter handler!! $E$ second message $E$";
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("server exceptionCaught method run ...");
        ctx.close();
    }
}
