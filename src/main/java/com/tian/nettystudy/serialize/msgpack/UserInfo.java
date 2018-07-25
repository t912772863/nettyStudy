package com.tian.nettystudy.serialize.msgpack;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2018/7/24 0024.
 */
public class UserInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    private String userName;
    private int userID;

    public UserInfo buildUserName(String userName){
        this.userName = userName;
        return this;
    }

    public UserInfo buildUserId(int userID){
        this.userID = userID;
        return this;
    }

    public final String getUserName(){
        return userName;
    }

    public final void setUserName(String userName){
        this.userName = userName;
    }

    public final int getUserID(){
        return userID;
    }

    public final void setUserID(int userID){
        this.userID = userID;
    }

    public byte[] codeC(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userID=" + userID +
                '}';
    }
}
