package com.tian.nettystudy.netty.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.IOException;

/**
 * Created by tianxiong on 2019/4/22.
 */
public final class MarshallingCodeCFactory {
    /**
     * 创建Marshalling解码器MarshallingDecoder
     * @return
     * @throws IOException
     */
    public static MarshallingDecoder buildMarshallingDecoder() throws IOException {
        // 参数"serial"表示创建的是java序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        // 设置一个版本号, 这样只有对应版本号编码和解码才能对应起来
        configuration.setVersion(5);

        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        // 1024表示单个序列化后最大长度
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);

        return decoder;
    }

    /**
     * 创建编码器
     * @return
     */
    public static MarshallingEncoder buildMarshallingEncode(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;

    }

}
