package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TestNettyDemo7Client {

    public static void main(String[] args) throws IOException {
        //客户端
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8081));
        System.out.println("waiting...");
        // socketChannel.write(StandardCharsets.UTF_8.encode("hahaha\nworld\nyuqinim\n"));
        // socketChannel.write(StandardCharsets.UTF_8.encode("my name is song yu qi, i am beautiful!\n"));
        System.in.read();
    }
}
