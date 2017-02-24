package com.ade.exp.mina.client;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by liyang on 17-2-24.
 */
public class MinaClient implements Runnable {

    private static final int port = 8031;

    public void start() {
        // 创建一个socket连接
        NioSocketConnector connector = new NioSocketConnector();
        // 获取过滤器链
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        ProtocolCodecFilter filter= new ProtocolCodecFilter(factory);
        // 添加编码过滤器 处理乱码、编码问题
        chain.addLast("objectFilter",filter);
        // 消息核心处理器
        connector.setHandler(new MinaClientHandler());
        // 设置链接超时时间
        connector.setConnectTimeoutCheckInterval(30);
        // 连接服务器，知道端口、地址
        ConnectFuture cf = connector.connect(new InetSocketAddress("192.168.101.135", port));
        // 等待连接创建完成
        cf.awaitUninterruptibly();
        cf.getSession().write("{\"data\":{\"innerCode\":\"01000032\",\"msgType\":\"register\"},\"from\":\"vmBox\",\"to\":\"kcs\",\"sn\":6240536155213791232,\"needResend\":false}");
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cf.getSession().write("{\"data\":{\"innerCode\":\"01000032\",\"msgType\":\"heart\"},\"from\":\"vmBox\",\"to\":\"kcs\",\"sn\":6240597366085255168,\"needResend\":false}");
        }
//        cf.getSession().getCloseFuture().awaitUninterruptibly();
//        connector.dispose();
    }

    @Override
    public void run() {
        start();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new MinaClient()).start();
        }
    }

}
