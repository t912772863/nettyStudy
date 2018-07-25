package com.tian.nettystudy.asynchronous;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/20 0020.
 */
public class TimeClientHandler implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    /**
     * 在构造函数中初始化NIO的多路复用器和SocketChannel对象. 需要注意的是创建SocketChannel之后, 需要将其设置为
     * 异步非阻塞模式.
     * @param host
     * @param port
     */
    public TimeClientHandler(String host, int port) {
        this.host = host ==null?"127.0.0.1":host;
        this.port = port;
        try{
            // 创建选择器
            selector = Selector.open();
            // 创建一个Socket通道
            socketChannel = SocketChannel.open();
            // 显示配置为非阻塞的
            socketChannel.configureBlocking(false);
        }catch (Exception e){
            e.printStackTrace();
            // 退出
            System.exit(1);
        }

    }

    public void run() {
        try{
            /*
            下面这行代码, 用于发送连接请求, 作为示例, 连接是成功的. 所以不需要做重连操作.
            因为将其放到循环之前.
             */
            doConnect();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectKeys.iterator();
                SelectionKey key = null;
                /*
                  在循环体中轮询多路复用器Selector, 当有就绪的Channel时, 执行handleInput(key)方法.
                 */
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }

        }
        /*
         线程退出后, 我们要对资源进行释放, 以实现优雅的退出. 下面几行代码用于对多路复用器的资源释放,
         由于多路复用器上可能注册成千上万的Channel或者Pipe, 如果一一对这些资源进行释放显然不合适, 因此,
         JDK底层会自动释放所有跟此多路复用器关联的资源

         */
        if(selector != null){
            try{
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 首先对SelectionKey进行判断, 看它处于什么状态, 如果是处于连接状态, 说明服务端已经返回ACK应答消息,
     * 这时我们需要对连接结果进行判断. 调用SocketChannel的finishConnect()方法. 如果返回值为true, 说明客
     * 户端连接成功, 如果返回值为false, 或者拋出io异常, 说明连接失败. 在本例中返回true,说明连接成功. 将
     * SocketChannel注册到多路复用器上, 注册SelectionKey.OP_READ操作位, 监听网络读操作. 然后发送请求消息
     * 给服务端.
     *
     *
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            // 判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(sc.finishConnect()){
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                }else {
                    // 连接失败进程退出
                    System.exit(1);
                }
            }
            /*
             如果客户端接收到了服务端的应答消息, 则SocketChannel是可读的, 由于无法事先判断应答码流的大小, 我们就
             预先分配了1MB的接收缓冲区用于读取应答消息, 调用SocketChannel的read()方法进行异步读取操作. 由于是异步
             操作,所以必需对读取的结果进行判断, 这部分的处理逻辑已经在前面介绍过了. 如果读取到了消息, 则对消息进行
             解码, 最后打印. 然后修改stop的值, 退出线程.

             */
            if(key.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Now is : "+body);
                    this.stop = true;
                }else if(readBytes < 0){
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                }else {
                    // 读到了0字节忽略
                }

            }

        }



    }

    /**
     * 首先对SocketChannel的connect()操作进行判断, 如果连接是成功的. 则将SocketChannel注册到多路复用器Selector上.
     * 注册SelectionKey.OP_READ. 如果没有直接连接成功, 则说明服务端没有返回TCP握手应答信息, 但这并不代表连接失败,
     * 我们需要将SocketChannel注册到多路复用器Selector上,注册SelectionKey.OP_CONNECT, 当服务端返回TCP syn-ack消息
     * 后, Selector就能够轮询到这个SocketChannel处理连接就绪状态.
     * @throws IOException
     */
    private void doConnect() throws IOException {
        // 如果直接连接成功, 则注册到多路复用器上, 发送请求消息, 读应答
        if(socketChannel.connect(new InetSocketAddress(host, port))){
            // 如果socket可以成功的连接到指定的地址, 则把这个socket通道注册到选择器上, 监听读取事件
            socketChannel.register(selector, SelectionKey.OP_READ);
            // 主动向外写消息
            doWrite(socketChannel);
        }else {
            // 如果socket没有成功连接到指定地址, 则把这个socket通道注册到选择器上, 监听连接事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }

    }

    /**
     * 我们构造请求消息体, 然后地其进行编码, 定稿到发送缓冲区中, 最后调用SocketChannel的write方法进行发送.
     * 由于发送是异步的. 所以会存在"半包写"问题, 最后通过hasRemaining()方法对发送结果进行判断, 如果缓冲区中
     * 的消息全部发送完成, 打印"Send order ..."
     *
     * @param sc
     * @throws IOException
     */
    private void doWrite(SocketChannel sc) throws IOException {
        // 把要写的消息先转成字节数组, 再转成缓冲字节对象
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        // 翻转对象
        writeBuffer.flip();
        // 写内容
        sc.write(writeBuffer);
        if(!writeBuffer.hasRemaining()){
            // 没有待写内容了, 输出日志
            System.out.println("Send order to server succeed.");
        }



    }


}
