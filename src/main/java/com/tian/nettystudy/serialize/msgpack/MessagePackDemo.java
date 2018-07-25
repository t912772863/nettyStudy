package com.tian.nettystudy.serialize.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MessagePack的使用示例
 *
 * Created by Administrator on 2018/7/24 0024.
 */
public class MessagePackDemo {
    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<String>();
        src.add("tianxiong");
        src.add("chenhaiying");
        src.add("tianshurui");
        // 序列化工具对象
        MessagePack msgpack = new MessagePack();
        // 序列化
        byte[] raw = msgpack.write(src);
        // 反序列化
        List<String> listStr = msgpack.read(raw, Templates.tList(Templates.TString));
        for (String s:listStr
             ) {
            System.out.println(s);
        }

    }
}
