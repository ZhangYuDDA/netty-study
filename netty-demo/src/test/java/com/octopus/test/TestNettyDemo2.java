package com.octopus.test;

import java.nio.ByteBuffer;

public class TestNettyDemo2 {

    public static void main(String[] args) {
        /*System.out.println(ByteBuffer.allocate(10).getClass());
        System.out.println(ByteBuffer.allocateDirect(10).getClass());*/
        ByteBuffer allocate = ByteBuffer.allocate(10);
        allocate.put(new byte[]{'a', 'b', 'c', 'd'});
        allocate.flip();
        /*allocate.get(new byte[4]);
        allocate.rewind();  //position重新回到开头
        System.out.println((char) allocate.get());*/

        /*allocate.get();
        allocate.get();
        allocate.mark();  //做标记

        allocate.get();
        allocate.get();
        allocate.reset();  //position回到开头位置
        System.out.println((char) allocate.get());*/

        //指定任意位置，不会修改position
        System.out.println((char) allocate.get(3));
    }
}
