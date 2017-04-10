package com.ade.exp.net.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 2017/4/10.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 此方法会在连接到服务器后被调用
     */
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!\n", CharsetUtil.UTF_8));
        ctx.writeAndFlush("Netty rocks!\n");
    }

    /**
     * 此方法会在接收到服务器数据后调用
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("Client received: " + msg);
    }

    /**
     * 捕捉到异常
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getLocalizedMessage(), cause);
        ctx.close();
    }

}
