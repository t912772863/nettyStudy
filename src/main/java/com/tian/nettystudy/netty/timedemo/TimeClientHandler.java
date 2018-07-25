package com.tian.nettystudy.netty.timedemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    private int counter;
    private byte[] req;

    private final ByteBuf firstMessage;

    public TimeClientHandler(){
        req = ("QUERY TIME ORDER"+"--------->").getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    /**
     * 当客户端和服务端的TCP链路建立成功以后, Netty的Nio线程会调用该方法
     *
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx){
        ByteBuf message = null;
        // 设计, 发送100次消息, 每发送一次都刷新一次
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            // 将请求消息发送给服务器
            ctx.writeAndFlush(message);
        }

    }

    /**
     * 当服务端返回应答消息 时, 该6方法会被调用 .
     *
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is: "+body+" ; the counter is : "+ ++counter);

    }

    /**
     * 发生异常时调用
     * @param ctx
     * @param cause
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        // 释放资源
        ctx.close();

    }


}
