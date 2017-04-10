package com.ade.exp.net.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.util.concurrent.Executors;

/**
 * Created by liyang on 2017/4/10.
 */
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int port = 8999;

    public void startUp() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap(); // 引导辅助程序
        EventLoopGroup group = new NioEventLoopGroup(); // 通过nio方式来接收连接和处理连接
        try {
            b.group(group)
                    .channel(NioServerSocketChannel.class) // 设置nio类型的channel
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() { //有连接到达时会创建一个channel
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new NettyServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();// 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            if (f.isSuccess()) {
                logger.info(this.getClass().getName() + " started and listen on " + f.channel().localAddress());
            }
            f.channel().closeFuture().sync();// 应用程序会一直等待，直到channel关闭
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            group.shutdownGracefully().sync();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }

    public static void main(String[] args) {
        try {
            new NettyServer().startUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
