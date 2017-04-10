package com.ade.exp.net.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 2017/4/10.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = (String) msg;
        logger.info(ctx.channel().remoteAddress().toString());
        logger.info("server received data :" + body);
        ctx.writeAndFlush("response.\n");//写回数据，
    }
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) //flush掉所有写回的数据
                .addListener(ChannelFutureListener.CLOSE); //当flush完成后关闭channel
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        logger.error(cause.getLocalizedMessage(), cause);
        ctx.close();//出现异常时关闭channel
    }

}
