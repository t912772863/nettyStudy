package com.tian.nettystudy.asynchronous;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO时间服务器.
 * Created by Administrator on 2018/7/20 0020.
 */
public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port){
        try{
            /*
            在构造方法中, 进行资源的初始化, 创建多路复用器selector, ServerSocketChannel,
            对Channel和TCP参数进行配置. 系统资源初始化成功以后, 将ServerSocketChannel注册
            到Selector, 监听SelectionKey.OP_ACCEPT操作位. 如果资源初始化失败, 则退出.
             */

            // 创建一个选择器, 通过它自身的open方法
            selector = Selector.open();
            // 打开一个服务端的socket通道
            serverSocketChannel = ServerSocketChannel.open();
            // 显示设定为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 获取ServerSocket, 并绑定ip和端口号, 指定请求队列的最大长度.
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 把socket注册到selector, 并标明事件为监听连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start at port: "+port);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    public void run() {
        while (!stop){
            try{
                /*在线程内循环遍历selector, 它的休眠时间为1秒, 无论是否有读写事件发生, selector每隔1s都被唤醒一次.
                  还提供了一个无参的方法: 当有处于就绪状态的Channel时, selector将返回Channel的SelectorKey集合, 通过
                  对就绪状态的Channel集合进行迭代. 可以进行网络的异步读写操作.
                 */
                selector.select(1000);
                // 返回选择器,的选定键集
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                // 遍历并处理
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
            }
        }
        // 多路复用器关闭以后, 所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭. 所以不需要重复释放资源.
        if(selector != null){
            try{
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        // 判断这个key的有效性, 一个key在创建时是有效的, 直到它被取消, 或者它的通道关闭, 或者它的选择器关闭.
        if(key.isValid()){
            /*
             处理新接入客户端的消息, 根据SelectionKey的操作位进行判断, 即可获知网络事件的类型,
             通过ServerSocketChannel的accept接收客户端的连接请求, 并创建SocketChannel实例, 完成上述
             操作后, 相当于完成了TCP的三次握手. TCP物理链路正式建立. 注意, 我们需要将新的SocketChannel
             设置为异步非阻塞, 同时也可以对TCP参数进行设置, 例如TCP接收和发送缓冲区的大小. 但是这里做
             为一个入门的例子, 就没有对上述的参数进行设置.

             */
            // 处理新接入的请求消息.
            if(key.isAcceptable()){
                // 接受新的连接
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // 把新的连接添加到Selector中, 监听读取内容事件
                sc.register(selector, SelectionKey.OP_READ);
            }
            /*
             下面这一部分代码用于读取客户端请求的消息, 首先创建一个ByteBuffer, 由于我们事先无法得知客户端发送的码流大小,
             作为例程, 我们开辟了一个1MB的缓冲区, 然后调用SocketChannel的read方法读取请求码流. 注意, 由于我们已经将SocketChannel
             设置为异步非阻塞, 因此它的read是非阻塞的. 使用返回值进行判断. 看读到的字节数, 返回值有以下三种可能.
             1. 返回值大于0: 读到了字节, 对字节进行编解码
             2. 返回值等于0: 没有读取到字节, 属于正常场景,直接忽略
             3. 返回值为-1, 链路已经关闭.需要关闭SocketChannel,释放资源.
             当读取到码流以后, 进行解码. 首先对readBuffer进行flip操作.它的作用是将缓冲区当前的limit设置为position, position设置为0.
             用于后续对缓冲区的读取操作. 然后根据缓冲区可读的字节个数创建字节数组. 调用ByteBuffer的get操作将缓冲区可读的字节
             数组复制到新创建的字节数组中, 最后调用字符串的构造函数创建请求消息体并打印.

             */
            if(key.isReadable()){
                // 读取数据, 先获取到SocketChannel
                SocketChannel sc = (SocketChannel) key.channel();
                // 创建一个缓冲字节对象,分配指定大小字节
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                // 从Socket通道中一次读取指定大小的数据
                int readBytes = sc.read(readBuffer);
                // 有读到数据
                if(readBytes > 0){
                    // 翻转对象
                    readBuffer.flip();
                    //readBuffer.remaining() 返回大小
                    byte[] bytes = new byte[readBuffer.remaining()];
                    // 读取字节内容, 放入到bytes数组中
                    readBuffer.get(bytes);
                    // 把字节数组, 根据指定编码集生成字符串
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order: "+body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";
                    // 回写消息
                    doWrite(sc, currentTime);
                }else if(readBytes < 0){
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                }else {
                    // 读到0字节, 忽略
                }
            }
        }
    }

    /**
     * 将应答消息异步发送给客户端, 首先将字符串编码成字节数组, 根据字节数组的容量创建ByteBuffer, 调用ByteBuffer的
     * put操作, 将字节数组复制到缓冲区, 然后缓冲区进行flip操作. 最后调用SocketChannel的write方法将绥中区中的字节数
     * 组发送出去, 需要指出的是, 由于SocketChannel是异步非阻塞的, 它并不保证一次能够把需要发送的字节数组发送完, 此时
     * 会出现"写半包"问题, 我们需要注册写操作, 不断轮询Selector将没有发送完的ByteBuffer发送完毕, 然后可以通过ByteBuffer
     * 的hasRemain()方法判断消息是否发送完成.
     *
     *
     * @param channel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if(response != null && response.trim().length() > 0){
            // 先把要回写的内容转成字节数组
            byte[] bytes = response.getBytes();
            // 创建一个指定大小的字节缓冲对象
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            // 把字节数组放入字节缓存对象中
            writerBuffer.put(bytes);
            // 翻转对象
            writerBuffer.flip();
            // 用Socket写字节缓冲对象
            channel.write(writerBuffer);
        }

    }

}
