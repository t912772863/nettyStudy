package com.tian.nettystudy.netty.protocol;

/**
 * Created by Administrator on 2018/7/26 0026.
 */
public enum  MessageType {
    SERVICE_REQ((byte)0),SERVICE_RESP((byte)1),ONE_WAY((byte)2),LOGIN_RESP((byte) 4), LOGIN_REQ((byte) 3),HEARTBEAT_REQ((byte)5) ,
    HEARTBEAT_RESP((byte) 6);


    private byte value;
    MessageType(byte value){
        this.value = value;
    }

    public byte value(){
        return this.value;
    }
}
