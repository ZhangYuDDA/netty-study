package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class TestNettyDemo10 {
    public static void main(String[] args) throws IOException {
        // 处理写数据，一次性写入过多数据
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8081));
        Selector selector = Selector.open();
        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scKey = socketChannel.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    //channel 可读可写
                    // 服务端向客户端写数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                    // 把buffer的内容写到channel中，不一定一次能写完
                    int write = socketChannel.write(buffer);
                    System.out.println(write);
                    if (buffer.hasRemaining()) { //没写完，不是让他循环等待直到能继续写为止，而是让他自己触发可写事件，只要channel可以继续写了，就触发一个可写事件
                        // 在原有事件上加上可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 为了让下次能获取到没写完的buffer
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) { //可写事件，上次没写完
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int write = channel.write(buffer);//继续写
                    System.out.println(write);
                    if (!buffer.hasRemaining()) { //都写完了
                        // 取消关注可写事件，不然下次通道可写时，又触发了
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        key.attach(null);
                    }
                }
            }
        }
    }
}
