package com.tian.nettystudy.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息解码器,
 *
 * 在这里我们用到了netty的LengthFieldBasedFrameDecoder解码器, 它支持自动的TCP粘包和半包处理, 只需要给出标识消息长度的字段
 * 偏移量和消息长度自身所占的字节数, netty就能自动实现对半包的处理. 对于业务解码器来说, 调用父类LengthFieldBasedFrameDecoder
 * 的解码方法后, 返回的就是整饬的消息或者空消息, 如果为空则说明是半包消息, 直接返回继续由IO线程读取后续的码流.
 *
 * Created by Administrator on 2018/7/26 0026.
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if(frame == null){
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if(size > 0){
            Map<String, Object> attch = new HashMap(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attch.put(key, marshallingDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }
        if(in.readableBytes() > 4){
            message.setBody(marshallingDecoder.decode(in));
        }
        message.setHeader(header);
        return message;
    }

}
