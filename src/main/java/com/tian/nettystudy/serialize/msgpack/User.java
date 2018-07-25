package com.tian.nettystudy.serialize.msgpack;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * Creaed by Administrator on 2018/7/25 0025.
 *
 * @Message注解是messagePack在解析对象的时候要用的注解, 否则会报错
 */
@Message
public class User implements Serializable{
    private static final long serialVersionUID = -2428890512566044767L;
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (age != user.age) return false;
        return name != null ? name.equals(user.name) : user.name == null;

    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
