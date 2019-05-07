package com.tian.nettystudy.netty.shangxuetang.serial;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by tianxiong on 2019/5/6.
 */
public class RequestMessage implements Serializable{
    private static final long serialVersionUID = 3624967666220469542L;
    private Long id;
    private String message;
    private byte[] attachment;

    public RequestMessage(Long id, String message, byte[] attachment){
        this.id = id;
        this.message = message;
        this.attachment = attachment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", attachment=" + Arrays.toString(attachment) +
                '}';
    }
}
