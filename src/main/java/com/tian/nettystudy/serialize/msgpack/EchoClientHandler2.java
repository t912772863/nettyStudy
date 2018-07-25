package com.tian.nettystudy.serialize.msgpack;

import com.tian.nettystudy.serialize.msgpack.User;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class EchoClientHandler2 extends ChannelHandlerAdapter {
    private final int sendNumber;
    public EchoClientHandler2(int sendNumber){
        this.sendNumber = sendNumber;
    }

    public void channelActive(ChannelHandlerContext ctx){
        User[] infos = UserInfo();
        for (User u:infos) {
            ctx.write(u);
        }
        ctx.flush();
    }

    private User[] UserInfo(){
        User[] userInfos = new User[sendNumber];
        User userInfo = null;
        for (int i = 0; i < sendNumber; i++) {
            userInfo = new User();
            userInfo.setAge(i);
            userInfo.setName("ABCDEFG----->"+i);
            userInfos[i] = userInfo;
        }
        return userInfos;

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("Client receive the msgpack message: "+msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }



}
