package com.octopus.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestNettyDemo {

    public static void main(String[] args) throws IOException {
        //获取channel
        //两种方式：输入输出流来获取  RandomAccessFile
        try (FileChannel channel = new FileInputStream("netty-demo/data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            int len = 0;
            while (len != -1) {
                len = channel.read(buffer);
                buffer.flip();  //切换到读模式，移动position到开头的位置
                while (buffer.hasRemaining()) {
                    System.out.println((char) (buffer.get()));
                }
                //继续写，切换到写模式
                buffer.clear();  //写的position又移到开头位置
                //如果是用compact，不是移到开头，而是将上次没有全部读完的数据先移到缓冲区前面，再把写的position移到下一个位置
                //buffer.compact();
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
