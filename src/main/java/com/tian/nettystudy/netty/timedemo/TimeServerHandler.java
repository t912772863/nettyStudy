package com.tian.nettystudy.netty.timedemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * TimeServerHandler继承自ChannelHandlerAdapter, 它用于对网络事件进行读写操作.
 * 通常我们只需要关注channelRead和exceptionCaught方法就可以了.
 *
 * Created by Administrator on 2018/7/23 0023.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    private int counter;

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*
         类型强转, ByteBuf类型于JDK中的ByteBuffer对象, 不过它提供了更加强大和灵活的功能. 通过ByteBuf的readableBytes
         方法可以获取缓冲区可读的字节数, 根据可读字节数, 创建数组. 通过ByteBuf的readBytes方法将缓冲区中的字节数组
         复制到到新建的byte数组中, 最后通过new String构建消息.
         */
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8").substring(0, req.length- "--------->".length());
        System.out.println("The time server receive order: "+body+" ;the counter is: "+ ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";
        currentTime = currentTime +"--------->";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);

    }

    public void channelReadComplete(ChannelHandlerContext ctx){
        /*
        下面这个flush方法的作用是将消息发磅队列中的消息写入到SocketChannel中发送给对方. 从性能角度考虑. 为了防止频繁的
        唤醒Selector进行消息发送, netty的write方法并不直接将消息写入SocketChannel中, 调用write方法只是把待发送的消息放
        到发送缓冲数组中, 再通过调用flush方法, 将发送缓冲区中的消息全部写到SocketChannel中.
         */
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        // 发生异常时, 关闭相关资源.
        ctx.close();
    }

}
