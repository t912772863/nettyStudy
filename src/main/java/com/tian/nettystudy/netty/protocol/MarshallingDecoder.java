package com.tian.nettystudy.netty.protocol;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * Netty协议消息解码工具类
 * Created by Administrator on 2018/7/27 0027.
 */
public class MarshallingDecoder {
    private final Unmarshaller unmarshaller;

    /**
     * Creates a new decoder whose maximum object size is {@code 1048576} bytes.
     * If the size of the received object is greater than {@code 1048576} bytes,
     *
     * @throws IOException
     *
     */
    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws Exception {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }

}
