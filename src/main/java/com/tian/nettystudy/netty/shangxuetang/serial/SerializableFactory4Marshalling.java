package com.tian.nettystudy.netty.shangxuetang.serial;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * Created by tianxiong on 2019/4/29.
 */
public class SerializableFactory4Marshalling {
    /**
     * 创建解码器
     * @return
     */
    public static MarshallingDecoder buildMarshallingDecoder() {
        // 首先通过Marshalling工具类的方法获取Marshalling实例对象, 参数serial标识创建的是java的序列化工厂对象.
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        // 创建了MarshallingConfiguration对象, 配置了版本号为5
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        // 只要使用jdk5以上版本, version只能定义为5
        configuration.setVersion(5);
        // 根据marshallerFactory和configuration创建provider
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        // 构建Netty的MarshallingDecoder对象, 两个参数分别为provider和单个序列化后的最大长度.
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024*1024*1);
        return decoder;
    }

    /**
     * 创建编码器
     * @return
     */
    public static MarshallingEncoder buildMarshallingEncoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        // 构建Netty的MarshallingEncoder对象, MarshallinEncoder用于实现序列化接口的POJO对象序列化为二进制
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }
}
