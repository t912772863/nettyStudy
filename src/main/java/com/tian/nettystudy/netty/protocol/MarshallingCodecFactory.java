package com.tian.nettystudy.netty.protocol;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * Created by Administrator on 2018/7/27 0027.
 */
public class MarshallingCodecFactory {
    /**
     * 创建Jboss Marshaller
     *
     * @return
     * @throws IOException
     */
    public static Marshaller buildMarshalling() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling
                .getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory
                .createMarshaller(configuration);
        return marshaller;
    }

    /**
     * 创建Jboss Unmarshaller
     *
     * @return
     * @throws IOException
     */
    public static Unmarshaller buildUnMarshalling() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
        return unmarshaller;
    }
}
