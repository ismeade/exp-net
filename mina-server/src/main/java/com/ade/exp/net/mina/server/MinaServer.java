package com.ade.exp.net.mina.server;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Created by liyang on 16-11-15.
 */
public class MinaServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static MinaServer instance = new MinaServer();

    private Integer  port;  // 端口号
    private int      processor = 5;     // 线程数量
    private int      pool      = 10;    // 线程池大小
    private int      size      = 10240; // 输入输出缓冲区的大小
    private int      max       = 10000; // 主服务监听端口的监听队列的最大值，多的连接来将被拒绝
    private int      length    = 10240; // 单条消息长度

    public static MinaServer getInstance() {
        return instance;
    }

    private MinaServer() {}

    private Lock lock = new ReentrantLock();

    public void startUp() {
        if (lock.tryLock()) {
            try {

                if (null == port || pool < 2000) {
                    port = 8031;
                    logger.info("keep server use default port:8031.");
                }
                NioSocketAcceptor acceptor = new NioSocketAcceptor(processor);
                Executor threadPool = Executors.newFixedThreadPool(pool);
                acceptor.getFilterChain().addLast("exector", new ExecutorFilter(threadPool));
                TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"));
                factory.setDecoderMaxLineLength(length);
                acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                acceptor.setReuseAddress(true);
                acceptor.getSessionConfig().setReuseAddress(true);
                acceptor.getSessionConfig().setReceiveBufferSize(size);
                acceptor.getSessionConfig().setSendBufferSize(size);
                acceptor.getSessionConfig().setTcpNoDelay(true);
                acceptor.setBacklog(max);
                acceptor.setDefaultLocalAddress(new InetSocketAddress(port));
                acceptor.setHandler(new MinaServerHandler());
                acceptor.bind();
                logger.info("keep service is running. port: " + port);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        } else {
            throw new RuntimeException("Service started.");
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public int getProcessor() {
        return processor;
    }

    public void setProcessor(int processor) {
        this.processor = processor;
    }

    public int getPool() {
        return pool;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
