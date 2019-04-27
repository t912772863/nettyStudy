package com.tian.nettystudy.netty.shangxuetang.fixlength;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class Server4FixedLengthHandler extends ChannelHandlerAdapter {
    /**
     * 业务处理逻辑
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        /**
         * 注意这里的消息就是一个String类型的, 是因为在使用这个hander的地方, 前面使用了一个StringDecoder,会把消息转换成一个字符串, 再走后面的handler
         */
        String message = msg.toString();
        System.out.println("from client: "+message);
        // 注意ok后面有一个空格,因为现在用的定长为3的
        String line = "ok ";
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("server exceptionCaught method run...");
        ctx.close();
    }

}
