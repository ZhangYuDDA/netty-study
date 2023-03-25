package com.octopus.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestNettyDemo5 {

    public static void main(String[] args) {
        //处理粘包、拆包现象
        ByteBuffer source = ByteBuffer.allocate(32);
        //收到第一个包
        source.put("hello world\nI'm zhangsan\nho".getBytes(StandardCharsets.UTF_8));//粘包
        split(source);
        //收到第二个包
        source.put("w are you?\nhaha!\n".getBytes()); //半包
        split(source);
    }

    //解决粘包
    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i - source.position() + 1;
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }
                target.flip();
                System.out.println(StandardCharsets.UTF_8.decode(target).toString());
            }
        }
        source.compact(); //可能有还剩下半包
    }
}
