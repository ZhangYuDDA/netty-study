package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestNettyDemo10Client {

    public static void main(String[] args) throws IOException {
        /*SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = sc.register(selector, SelectionKey.OP_READ);
        //selectionKey.interestOps(SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        sc.connect(new InetSocketAddress("localhost", 8081));
        int count = 0;
        while (true) {
            int select = selector.select();  //这里一直返回0，不知道为什么？？？？？？？？
            System.out.println(select);
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    count += channel.read(buffer);
                    System.out.println(count);
                    buffer.clear();
                }
            }
        }*/
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8081));
        int count = 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count += sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }
    }
}
