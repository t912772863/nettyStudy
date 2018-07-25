package com.tian.nettystudy.asynchronous;

/**
 * NIO创建的TimeClient源码分析
 * Created by Administrator on 2018/7/19 0019.
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {
                // 采用默认值
            }
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-001").start();


    }

}
