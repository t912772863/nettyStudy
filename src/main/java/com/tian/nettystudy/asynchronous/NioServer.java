package com.tian.nettystudy.asynchronous;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务端主要的创建过程说明
 *
 * 本类中的示例, 只是对常用方法做一个展示, 可能无法正常编译
 * Created by Administrator on 2018/7/19 0019.
 */
public class NioServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        // 一. 打开ServerSocketChannel, 用于监听客户端的连接, 它是所有客户端连接的父管道.
        ServerSocketChannel acceptorServer = ServerSocketChannel.open();
        // 二. 绑定监听端口, 设置连接为非阻塞连接,
        acceptorServer.socket().bind(new InetSocketAddress(InetAddress.getByName("IP"), port));
        acceptorServer.configureBlocking(false);
        // 三. 创建Reactor线程, 创建多路复用器, 并启动线程.
        Selector selector = Selector.open();
//        new Thread(new ReactorTask()).start();
        // 四. 将ServerSocketChannel注册到Reactor线程的多路复用器selector上, 监听accept事件.
//        SelectionKey key = acceptorServer.register(selector, SelectionKey.OP_ACCEPT,ioHandler);
        // 五. 多路复用器在线程run方法中无限循环轮询准备就绪的key,
        int num = selector.select();
        Set selectedKeys = selector.selectedKeys();
        Iterator it = selectedKeys.iterator();
        while (it.hasNext()){
            SelectionKey s = (SelectionKey)it.next();
        }
        // 六. 多路复用器监听到有新的客户端接入, 处理新的接入请求, 完成TCP三次握手, 建立物理链路.
        SocketChannel channel = acceptorServer.accept();
        // 七. 设置客户端链路为非阻塞模式.
        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
        // 八. 将新接入的客户端连接注册到Reactor线程的多路复用器上, 监听读操作.读取客户端发送的网络消息.
//        SelectionKey key2 = acceptorServer.register(selector, SelectionKey.OP_READ, ioHandler);
        // 九. 异步读取客户端请求消息到缓冲区
//        int readNumber = channel.read(receivedBuffer);
        // 十. 对ByteBuffer进行编解码, 如果有半包消息指针reset, 继续读取后续的报文, 将解码成功的消息封装成Task, 投递到业务
        // 线程池中, 进行业务逻辑编排.
        Object message = null;
//        while(buffer.hasRemain){
//            byteBuffer.mark();
//            message = decode(byteBuffer);
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
        // 十一. 将POJO对象encode成ByteBuffer, 调用SocketChannel的异步write接口, 将消息异步发送给客户端.
//        socketChannel.write(buffer);


    }

}
