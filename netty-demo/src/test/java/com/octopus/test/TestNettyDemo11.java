package com.octopus.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TestNettyDemo11 {

    //多线程版本
    // 之前是把建立连接和读写都放在了一个线程，现在建立连接单独一个线程，读写可以有多个线程
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8081));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        Worker worker = new Worker("worker-0");
        Worker worker1 = new Worker("worker-1");
        Worker[] workers = new Worker[]{worker, worker1};
        AtomicInteger index = new AtomicInteger();
        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    //worker.register(sc);
                    //这行代码存在问题，无法保证该这行代码和子线程的selector.select();谁先执行，
                    //如果selector.select();先执行了，则会阻塞住，那么register就无法执行
                    //sc.register(worker.selector, SelectionKey.OP_READ);
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean isStart;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!isStart) {
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                isStart = true;
            }
            queue.offer(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //唤醒selector
            selector.wakeup();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            System.out.println(thread.getName() +": "+ StandardCharsets.UTF_8.decode(buffer));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
