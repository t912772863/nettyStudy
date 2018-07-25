package com.tian.nettystudy.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        /*
        把AsynchronousSocketChannel当成成员变量用, 主要用于读取半包消息和发送应答
         */
        if(this.channel == null){
            this.channel = channel;
        }
    }

    /**
     * 读取消息扣的处理
     * @param result
     * @param attachment
     */
    public void completed(Integer result, ByteBuffer attachment) {
        /*
        首先对attachment进行flip, 为后续从缓冲区读取数据做准备, 根据缓冲区的可读字节数创建byte数组.然后通过new String
        创建请求消息, 对请求消息进行判断.

         */
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try{
            String req = new String(body, "UTF-8");
            System.out.println("The time server receive order: "+req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)?new Date().toString():"BAD ORDER";
            doWrite(currentTime);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doWrite(String currentTime) {
        if(currentTime != null && currentTime.trim().length() >0){
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            writerBuffer.put(bytes);
            writerBuffer.flip();
            channel.write(writerBuffer, writerBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                public void completed(Integer result, ByteBuffer buffer) {
                    // 如果没有发送完成,继续发送
                    if(buffer.hasRemaining()){
                        channel.write(buffer,buffer,this);
                    }
                }

                public void failed(Throwable exc, ByteBuffer attachment) {
                    try{
                        channel.close();
                    }catch (Exception e){
                        // ingnore
                    }

                }
            });

        }


    }

    public void failed(Throwable exc, ByteBuffer attachment) {
        try{
            this.channel.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
