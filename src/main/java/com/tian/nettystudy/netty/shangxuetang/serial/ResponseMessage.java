package com.tian.nettystudy.netty.shangxuetang.serial;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by tianxiong on 2019/5/6.
 */
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 3682485726759042484L;
    private Long id;
    private String message;
    private byte[] attachment;

    public ResponseMessage(Long id, String message){
        this.id = id;
        this.message = message;
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
        return "ResponseMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", attachment=" + Arrays.toString(attachment) +
                '}';
    }
}
