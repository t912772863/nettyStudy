package com.tian.nettystudy.notasynchronous;

import com.tian.nettystudy.block.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 在服务端, 通过对代码进行一些改造, 用线程池技术,
 * 把连接过来的多个socket包装成task,然后交给线程池处理, 这样就不用对每个连接创建一个线程了,
 * 但是由于底层系统在处理的时候依然是采用的同步阻塞模型, 所以称为伪异步
 * Created by Administrator on 2018/7/19 0019.
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port =8080;
        try {
            if(args != null && args.length > 0){
                port = Integer.valueOf(args[0]);
            }
        }catch (Exception e){
            //
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start at port: "+port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);

            while (true){
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }

        }catch (Exception e){

        }finally {
            if(server != null){
                System.out.println("The time server close.");
                server.close();
                server = null;
            }
        }


    }
}
