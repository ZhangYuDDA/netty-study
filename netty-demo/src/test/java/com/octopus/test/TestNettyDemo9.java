package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class TestNettyDemo9 {

    public static void main(String[] args) throws IOException {
        //使用selector实现多路复用
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8081));
        ssc.configureBlocking(false);  //必须工作在非阻塞模式
        Selector selector = Selector.open();
        //selectionKey 在事件发生后，通过它可以知道事件和哪个channel发生的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);//注册selector
        //key 只关注accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        System.out.println(sscKey);

        while (true) {
            //要注意，如果事件没处理，那么select()方法会一直不阻塞
            selector.select(); //没有事件发生时会阻塞, 由于前面设置了只关注accept事件，所以只会在建立连接时才停止阻塞
                               //后面selector又绑定了其他事件
            //selectedKeys 包含所有的事件, 事件发生了，对应的selectionKey就会放到集合中
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 重要！！！！！！！！！！！！！！ 事件处理完成后，selectedKeys的集合中对应的SelectionKey不会删除，所以必须要手动删除
                iterator.remove();
                //判断是accept事件
                if (key.isAcceptable()) {
                    //可以取消事件, cancel是直接把这个key从selector中删除了，因此key关注的事件无法再次获取到了
                    //key.cancel();
                    System.out.println(key);
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    //建立连接后返回新的SocketChannel，这个SocketChannel同样也要注册到selector
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);  // buffer作为附件放到selector中，这样就能关联到对应的SocketChannel
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);  //让scKey关注read事件
                } else if (key.isReadable()) {  //当前是读事件
                    // 无论是客户端正常关闭还是异常关闭，都会产生一个读事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);  //消息存在边界问题，粘包，或者半包，如果buffer存不下，剩余的内容会再次出发读事件
                        if (read == -1) { //如果是正常关闭，read结果会返回-1
                            key.cancel();
                        } else {
                            //buffer.flip();
                            split(buffer);
                            // 如果 一条消息都放不下buffer，进行扩容
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                //重新作为附件
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        // 异常关闭会抛IOException
                        e.printStackTrace();
                        // 但是由于异常关闭会产生一个读事件，如果没处理，selector.select()会一直不阻塞
                        key.cancel();
                    }
                }

            }
        }
    }

    //解决粘包
    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i - source.position() + 1;
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }
                target.flip();
                System.out.println(StandardCharsets.UTF_8.decode(target).toString());
            }
        }
        source.compact(); //可能有还剩下半包
    }
}
