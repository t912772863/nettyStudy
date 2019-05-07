package com.tian.nettystudy.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class AsyncTimeClientHandler implements Runnable, CompletionHandler<Void, AsyncTimeClientHandler> {
    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;


    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try{
            //  AsynchronousSocketChannel.open()创建一个新的AsynchronousSocketChannel对象
            client = AsynchronousSocketChannel.open();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void run() {
        // 创建CountDownLatch进行等待, 防止操作还没有完成就退出线程.
        latch = new CountDownLatch(1);
        /*
        通过connect方法, 发起异步操作
         */
        client.connect(new InetSocketAddress(host, port), this, this);
        try{
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异步连接成功之后的方法回调
     *
     * @param result
     * @param attachment
     */
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writerBuffer = ByteBuffer.allocate(req.length);
        writerBuffer.put(req);
        writerBuffer.flip();
        // 异步写
        client.write(writerBuffer, writerBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()){
                    client.write(buffer, buffer, this);
                }else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        public void completed(Integer result, ByteBuffer buffer) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            String body;
                            try{
                                body = new String(bytes, "UTF-8");
                                System.out.println("Now is: "+body);
                                latch.countDown();
                            }catch (Exception e){
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }


                        }

                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try{
                                client.close();
                                latch.countDown();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }

            }

            public void failed(Throwable exc, ByteBuffer attachment) {
                try{
                    client.close();
                    latch.countDown();
                }catch (Exception e){

                }
            }
        });


    }

    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        try {
            client.close();
            latch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
