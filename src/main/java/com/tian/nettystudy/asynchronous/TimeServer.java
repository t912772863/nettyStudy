package com.tian.nettystudy.asynchronous;

/**
 * NIO创建的TimeServer原码分析
 *
 * 对比发现, NIO版本的代码比阻塞版本的代码要复杂的多, 但是应用越来越多, 主要有以下几个原因:
 * 1. 客户端发起的连接操作是异步的, 可以通过在多路复用器注册OP_CONNECT等待后续结果, 不需要像之前的客户端那样
 *    被阻塞.
 * 2. SocketChannel的读写操作都是异步的.如果没有可读写的数据它不会同步等待, 直接返回, 这样IO通信线程就可以处理其它的链路,
 *    不需要同步等待这个链路可用.
 * 3. 线程模型的优化, 由于JDK的Selector中Linux等主流操作系统上通过epoll实现, 它没有连接句柄数的限制(只受限于操作系统的
 *    最大句柄数或者对单个进程的句柄的限制), 这意味着一个Selector线程可以同时处理成千上万个客户连接, 而且性能不会随着
 *    客户端的增加而线性下降, 因此, 它非常适合做高性能, 高负载的网络服务器.
 *
 *
 * Created by Administrator on 2018/7/20 0020.
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();


    }

}
