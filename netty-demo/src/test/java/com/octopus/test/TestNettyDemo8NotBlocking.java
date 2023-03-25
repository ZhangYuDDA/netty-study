package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestNettyDemo8NotBlocking {

    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //非阻塞模式下，单线程
        //存在的问题，线程一直在走

        //使用ServerSocketChannel创建服务
        ServerSocketChannel ssc = ServerSocketChannel.open();  //服务器
        //服务器绑定端口
        ssc.bind(new InetSocketAddress(8081));
        ssc.configureBlocking(false);  //设置为非阻塞

        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //监听
            SocketChannel channel = ssc.accept();  //非阻塞的
            if (channel != null) {
                channels.add(channel);
                channel.configureBlocking(false);  //设置非阻塞
            }
            for (SocketChannel sc : channels) {
                int read = sc.read(buffer);  //非阻塞
                if (read > 0) {
                    buffer.flip();
                    System.out.println(StandardCharsets.UTF_8.decode(buffer));
                    buffer.clear();

                }
            }

        }
    }
}
