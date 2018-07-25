package com.tian.nettystudy.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * 同步阻塞IO的TimeServerHandler
 * Created by Administrator on 2018/7/19 0019.
 */
public class TimeServerHandler implements Runnable {
    private Socket socket;

    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true){
                body = in.readLine();
                if( body == null){
                    break;
                }
                System.out.println("The time server receive order: "+ body);
                currentTime = "QUERY TIME ORDER".equals(body)?new Date().toString():"BAD ORDER";
                out.println(currentTime);
            }

        }catch (Exception e){
            if(in != null){
                try{
                    in.close();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
            if(out != null){
                out.close();
                out = null;
            }
            if(this.socket != null){
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
