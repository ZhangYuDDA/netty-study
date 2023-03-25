package com.octopus.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestNettyDemo6 {

    public static void main(String[] args) {
        //filechannel 数据传输
        try (FileChannel from = new FileInputStream("netty-demo/data.txt").getChannel();
             FileChannel to = new FileOutputStream("netty-demo/target.txt").getChannel()) {
            for (long left = from.size(); left > 0;) {
                //最大只能2g，也就是说，to指定的文件大小只能到2g
                left -= from.transferTo(from.size() - left, left,to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
