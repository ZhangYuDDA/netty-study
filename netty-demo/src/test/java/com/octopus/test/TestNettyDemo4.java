package com.octopus.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestNettyDemo4 {

    public static void main(String[] args) {
        //分散读
        /*try (FileChannel channel = new RandomAccessFile("netty-demo/3parts.txt", "rw").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            System.out.println(StandardCharsets.UTF_8.decode(b1).toString());
            System.out.println(StandardCharsets.UTF_8.decode(b2).toString());
            System.out.println(StandardCharsets.UTF_8.decode(b3).toString());
        } catch (IOException e) {
        }*/

        //集中写
        try (FileChannel channel = new RandomAccessFile("netty-demo/3parts.txt", "rw").getChannel()) {
            ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
            ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
            ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
    }
}
