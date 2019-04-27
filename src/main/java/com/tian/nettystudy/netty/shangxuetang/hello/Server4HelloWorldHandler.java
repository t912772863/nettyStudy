package com.tian.nettystudy.netty.shangxuetang.hello;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * 也可以实现ChannelHandler接口, 但是要写的方法比较多, 所以一般用继承,只重写自己关注的方法就可以了
 *
 * ChannelHandler.Sharable代表当前handler是一个可以分享的处理器, 也就意味着, 服务器注册些handler后, 可以分享给多个客户端使用.
 * 如果不使用注解描述类型, 则每次客户端请求时, 必须为客户端重新创建一个新的handler对象.如果handler是一个Sharable的, 要注意避免
 * 定义可写的属性变量, 因为非线程安全的
 * Created by tianxiong on 2019/4/22.
 */
@ChannelHandler.Sharable
public class Server4HelloWorldHandler extends ChannelHandlerAdapter {
    /**
     * 业务处理逻辑,
     * 用于处理读取数据请求的逻辑.
     * @param ctx 上下文对象, 其中包含与客户端建立连接的所有资源. 如:对应的Channel
     * @param msg 读取到的数据, 默认类型是ByteBuf, 这个类是netty对ByteBuffer进行的封装.不需要考虑复位问题.
     * @throws UnsupportedEncodingException
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        // 强加获取的数据
        ByteBuf readBuffer = (ByteBuf) msg;
        // 创建一个字节数据,用于保存缓冲中的数据
        byte[] tempDatas = new byte[readBuffer.readableBytes()];
        // 将缓存中的数据到字节数组中.
        readBuffer.readBytes(tempDatas);
        String message = new String(tempDatas, "UTF-8");
        System.out.println("from client: "+message);
        if("exit".equals(message)){
            ctx.close();
            return;
        }

        String line = "server message to client!";
        // 写操作自动释放缓存, 避免内存溢出问题.
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
        // 也可以用write进行多次写,最后再flush发送
    }

    /**
     * 异常处理逻辑. 当客户端异常退出的时候也会运行.
     * @param ctx 上下文件关闭,资源也会关闭
     * @param cause
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("server exceptionCaught method run...");
        ctx.close();
    }
}
