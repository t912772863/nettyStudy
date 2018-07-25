package com.tian.nettystudy.asynchronous;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO客户端示例代码
 * Created by Administrator on 2018/7/20 0020.
 */
public class NioClient {
    public static void main(String[] args) throws IOException {
        // 一. 打开SocketChannel, 绑定客户端本地地址(可先, 默认系统会随机分配一个可用的本地地址),
        SocketChannel clientChannel = SocketChannel.open();
        // 二. 设置SocketChannel为非阻塞模式, 同时设置客户端连接的TCP参数.
        clientChannel.configureBlocking(false);
//        socket.setReuseAddress(true);
//        socket.setReceiveBufferSize(1024);
//        socket.setSendBufferSize(1024);
        // 三. 异步连接服务端
        int port = 8080;
        boolean connected = clientChannel.connect(new InetSocketAddress("ip", port));
        // 四. 判断是否连接成功, 如果连接成功, 则直接注册读状态位到多路复用器中, 如果当前没有连接成功(异步
        //     连接, 返回false, 说明客户端已经发送sync包, 服务端没有返回ack包, 物理链路还没有建立),
//        if(connected){
//            clientChannel.register(selector, SelectionKey.OP_READ, ioHandler);
//        }else {
//            clientChannel.register(selector, SelectionKey.OP_CONNECT, ioHandler);
//        }
        // 五. 向Reactor线程的多路复用器注册OP_CONNECT状态位, 监听服务端的TCP ACK应答
//        clientChannel.register(selector, SelectionKey.OP_CONNECT, ioHandler);
        // 六. 创建Reactor线程, 创建多路复用器并启动线程,
        Selector selector = Selector.open();
//        new Thread(new ReactorTask()).start();
        // 七. 多路复用器在线程run方法的无限循环体内轮询准备就绪的key,
        int num = selector.select();
        Set selectorKeys = selector.selectedKeys();
        Iterator it = selectorKeys.iterator();
        while (it.hasNext()){
            SelectionKey key = (SelectionKey) it.next();
        }
        // 八. 接收connect事件并进行处理.
//        if(key.isConnectable()){

//        }
        // 九. 判断连接结果, 如果连接成功, 注册读事件到多路复用器
//        if(channel.flnishConnect()){
//            registerRead();
//        }
        // 十. 注册读事件到多路复用器中.
//        clientChannel.register(selector,SelectionKey.OP_READ, ioHandler);
        // 十一. 异步读取客户端信息到缓冲区.
//        int readNumber = channel.read(receivedBuffer);
        // 十二. 对ByteBuffer进行编解码, 如果有半包消息接收缓冲区reset,继续读取后续的报文, 将解码成功的消息封装成
        //       task, 投递到业务线程中, 进行逻辑编排.
        Object message = null;
//        while (buffer.hasRemain()){
//            byteBuffer.mark();
//            Object message = decode(byteBuffer);
//            if(message == null){
//                byteBuffer.reset();
//                break;
//            }
//            messageList.add(message);
//        }
//        if(!byteBuffer.hasRemain()){
//            byteBuffer.clear();
//        }else {
//            byteBuffer.compact();
//        }
//        if(messageList != null && !messageList.isEmpty()){
//            for (Object o:messageList) {
//                handlerTask(o);
//            }
//        }
        // 十三. 将POJO对象encode成ByteBuffer, 调用SocketChannel的异步write接口, 将消息异步发送给客户端
//        socketChannel.write(buffer);


    }

}
