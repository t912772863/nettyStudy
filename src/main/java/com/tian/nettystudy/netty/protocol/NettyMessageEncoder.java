package com.tian.nettystudy.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.Map;

/**
 * 消息编码类
 * Created by Administrator on 2018/7/26 0026.
 */
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {
    MarshallingEncoder marshallingEncoder;
//    MessagePack messagePack = new MessagePack();

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {
        if(msg == null || msg.getHeader() == null){
            throw new Exception("The encoder message is null.");
        }
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionId());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for(Map.Entry<String, Object> param: msg.getHeader().getAttachment().entrySet()){
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
           value = param.getValue();

//            byte[] byteArr = messagePack.write(value);
//            sendBuf.writeInt(byteArr.length);
//            sendBuf.writeBytes(byteArr);
            marshallingEncoder.encode(value, sendBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if(msg.getBody() != null){
//            byte[] byteArr = messagePack.write(msg.getBody());
//            sendBuf.writeInt(byteArr.length);
//            sendBuf.writeBytes(byteArr);
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        }else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes());
    }
}
