package com.octopus.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestNettyDemo3 {
    public static void main(String[] args) {
        //ByteBuffer 和 字符串的转换
        //1
        ByteBuffer buff = ByteBuffer.allocate(16);
        buff.put("hello".getBytes(StandardCharsets.UTF_8));

        //2  会自动切换读模式
        ByteBuffer buffer = StandardCharsets.UTF_8.encode("hello");

        //3, 包装， 会自动切换读模式
        ByteBuffer buffer1 = ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8));

        // decode注意buffer要切换为读模式
        String s = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(s);
    }
}
