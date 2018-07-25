package com.tian.nettystudy.serialize.msgpack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 测试序列化的一些性能问题
 * Created by Administrator on 2018/7/24 0024.
 */
public class TestUserInfo {
    public static void main(String[] args) throws IOException {
        test2();

    }

    /**
     * 方法一, 测试对比出, java自带的序列化方法,编码流特别大, 占用内容多
     * @throws IOException
     */
    public static void test1() throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserId(100).buildUserName("Welcome to netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is: "+ b.length);
        bos.close();
        System.out.println("---------------------------------------");
        System.out.println("The byte array serializable length is : "+info.codeC().length);
    }

    /**
     * 测试方法二, 通过多次序列化, 对比耗时, 发现java序列化速度太慢了
     */
    public static void test2() throws IOException {
        UserInfo info = new UserInfo();
        // 执行次数
        int times = 1000000;
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            info.buildUserId(100).buildUserName("Welcome to netty");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
        }
        System.out.println("用jdk自带的序列化方法, 执行"+times+"次序列化, 用时: "+(System.currentTimeMillis() - start1));

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            info.codeC();
        }
        System.out.println("用重写的序列化方法, 执行"+times+"次序列化, 用时: "+(System.currentTimeMillis() - start2));
    }


}
