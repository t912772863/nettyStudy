package com.tian.nettystudy.netty.protocol;


/**
 * Netty使用到的数据结构
 * Created by Administrator on 2018/7/26 0026.
 */
public class NettyMessage {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private Object body;
    public final Header getHeader(){
        return header;
    }

    public final void setHeader(Header header){
        this.header = header;
    }

    public final Object getBody(){
        return this.body;
    }

    public final void setBody(Object body){
        this.body = body;
    }

    public String toString(){
        return "NettyMessage [ header = "+header+"]";
    }
}
