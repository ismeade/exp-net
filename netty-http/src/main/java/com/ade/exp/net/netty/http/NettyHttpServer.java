package com.ade.exp.net.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 未完成
 * Created by liyang on 2017/4/11.
 */
public class NettyHttpServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int port = 8080;

    public void startUp() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap(); // 引导辅助程序
        String osName = System.getProperty("os.name");
        EventLoopGroup bossLoop = null;
        EventLoopGroup workerLoop = null;
        if (osName.equals("Linux")) {
            bossLoop = new EpollEventLoopGroup();
            workerLoop = new EpollEventLoopGroup();
        } else {
            bossLoop = new NioEventLoopGroup();
            workerLoop = new NioEventLoopGroup();
        }
        try {
            if (osName.equals("Linux")) { //Linux平台用Epoll模式
                b.channel(EpollServerSocketChannel.class);
            } else {
                b.channel(NioServerSocketChannel.class);
            }
            b.group(bossLoop, workerLoop)
                    .childHandler(new ChannelInitializer<SocketChannel>() { //有连接到达时会创建一个channel
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                            ch.pipeline()
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new HttpServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();// 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            if (f.isSuccess()) {
                logger.info(this.getClass().getName() + " started and listen on " + f.channel().localAddress());
            }
            f.channel().closeFuture().sync();// 应用程序会一直等待，直到channel关闭
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            bossLoop.shutdownGracefully().sync();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
            workerLoop.shutdownGracefully().sync();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }

    public static void main(String[] args) {
        try {
            new NettyHttpServer().startUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
