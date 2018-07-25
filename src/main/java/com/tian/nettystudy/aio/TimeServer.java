package com.tian.nettystudy.aio;

/**
 * NIO 2.0 也可以称为AIO, 来实现前面的功能
 *
 * Created by Administrator on 2018/7/23 0023.
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(timeServerHandler, "AIO-AsyncTimeServerHandler-001").start();

    }

}
