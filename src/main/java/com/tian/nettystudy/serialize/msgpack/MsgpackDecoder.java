package com.tian.nettystudy.serialize.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * MessagePack解码器开发.
 *
 *
 *
 * Created by Administrator on 2018/7/24 0024.
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf>{
    /**
     * 首先从msg中获取需要解码的byte数组, 然后调用MessagePack的read方法, 将其反序列化为Object对象
     * 将解码后的对象加入到out列表中, 这样就完成了解码操作.
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        msg.getBytes(msg.readerIndex(), array, 0, length);
        MessagePack msgpack = new MessagePack();
        out.add(msgpack.read(array));


    }
}
