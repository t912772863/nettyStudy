package com.tian.nettystudy.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class AsyncTimeServerHandler implements Runnable{
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try{
            /*
            创建一个异步的服务端通道, 然后调用bind方法绑定端口
             */
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            // 绑定一个地址和端口号
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start at port: "+port);

        }catch (Exception e){

        }

    }

    public void run() {
        /*
         创建一个等待条件, 防止还没处理, 线程就结束了.
         */
        latch = new CountDownLatch(1);
        doAccept();
        try{
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }



    }
    public void  doAccept(){
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }

}
