package com.tian.nettystudy.netty.marshalling;

/**
 * Created by tianxiong on 2019/4/22.
 */
public class MsgObject {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "MsgObject{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
