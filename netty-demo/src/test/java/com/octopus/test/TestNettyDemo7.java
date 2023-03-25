package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestNettyDemo7 {

    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //阻塞模式下，单线程
        //filechannel 只支持阻塞模式
        //使用ServerSocketChannel创建服务
        ServerSocketChannel ssc = ServerSocketChannel.open();  //服务器
        //服务器绑定端口
        ssc.bind(new InetSocketAddress(8081));

        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //监听
            SocketChannel channel = ssc.accept();  //阻塞，如果在这里阻塞，那么已经建立连接的通道无法读取内容
            channels.add(channel);
            for (SocketChannel sc : channels) {
                sc.read(buffer);  //如果没有内容，read也会阻塞，同样的如果在这里阻塞，就无法建立新的连接 ----》 这就是单线程阻塞模式的弊端
                                  //一个解决方法就是，读取的时候使用其他的线程
                buffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();

            }

        }
    }
}
