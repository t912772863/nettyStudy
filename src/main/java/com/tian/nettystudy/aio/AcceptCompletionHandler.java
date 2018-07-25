package com.tian.nettystudy.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {
    public void completed(AsynchronousSocketChannel result,
                          AsyncTimeServerHandler attachment) {
        /*
         我们从accachment获取成员变量asynchronousServerSocketChannel,然后继续调用它的accept方法, 这是因为调用
         asynchronousServerSocketChannel的accept方法后, 如果新的客户端连接接入, 系统将回调我们传入的CompletionHandler
         实例的completed方法, 表示新的客户端已经接入成功. 因为一个asynchronousServerSocketChannel可以接入成千上万
         个客户端, 所以需要继续调用它的accept方法, 接收其它客户端连接, 最终形成一个循环, 每当接入一个客户端连接成功后
         再异步接收新的客户端连接.

         */
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        /*
        链路建立以后, 服务端需要接收客户端的消息, 这里创建一个1024大小的缓冲对象, 然后通过read方法进行异步的读取.
         */
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /*
         read方法分析:
         参数1, 接收缓冲区, 用于从异步Channel中读取数据包.
         参数2, 异步Channel携带的附件, 通知回调的时候做为入参使用.
         参数3, 接收回调通知的业务handler, 在本例程中为ReadCompletionHandler
         */
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
