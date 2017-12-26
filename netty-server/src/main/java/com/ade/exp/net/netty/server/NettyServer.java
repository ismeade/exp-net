package com.ade.exp.net.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 * Created by liyang on 2017/4/10.
 */
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int port = 8999;

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
                    .option(ChannelOption.SO_BACKLOG, 1024) // 阻塞大小
                    .childHandler(new ChannelInitializer<SocketChannel>() { //有连接到达时会创建一个channel
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024)) // 解决粘包问题，行读取，以换行符结尾 1024代表读取最大长度，如果超过长度仍然没有换行符 则抛出异常并清空之前读到的字节
//                                    .addLast(new ReadTimeoutHandler(60)) // 空闲超时时间...
//                                    .addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$_".getBytes()))) // 使用自定义解码器
//                                    .addLast(new FixedLengthFrameDecoder(20)) // 定长解码器
                                    .addLast(new IdleStateHandler(12, 12, 10, TimeUnit.SECONDS))
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
            bossLoop.shutdownGracefully().sync();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
            workerLoop.shutdownGracefully().sync();//关闭EventLoopGroup，释放掉所有资源包括创建的线程
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
