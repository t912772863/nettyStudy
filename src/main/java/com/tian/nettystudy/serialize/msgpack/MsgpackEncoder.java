package com.tian.nettystudy.serialize.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * MessagePack 编码器的开发
 *
 * 继承自MessageToByteEncoder, 它负责将Object类型的对象编码为byte数组, 然后写入到ByteBuf中.
 * Created by Administrator on 2018/7/24 0024.
 */
public class MsgpackEncoder  extends MessageToByteEncoder<Object>{
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        MessagePack msgpack = new MessagePack();
        // 序列化
        byte[] raw = null;
        try{
            raw = msgpack.write(msg);
        }catch (Exception e){
            e.printStackTrace();
        }

        out.writeBytes(raw);
    }
}
