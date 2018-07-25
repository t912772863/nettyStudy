package com.tian.nettystudy.block;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞式IO,创建的TimeServer源码分析
 * Created by Administrator on 2018/7/19 0019.
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if(args != null && args.length>0){
            try {
                port = Integer.valueOf(args[0]);
            }catch (Exception e){
                // 采用默认值
            }
        }
        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            System.out.println("The time server is start at port: "+port);
            Socket socket = null;
            while (true){
                // 如果没有客户端接入, 则主线程会在下面这行代码阻塞
                socket = server.accept();
                // 这种同步模型, 每个socket连接都要创建一个线程来处理, 所以很难实现高并发的需求.
                new Thread(new TimeServerHandler(socket)).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(server != null){
                System.out.println("The time server close.");
                server.close();
                server = null;
            }
        }

    }
}
