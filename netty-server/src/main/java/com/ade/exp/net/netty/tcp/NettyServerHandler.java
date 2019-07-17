package com.ade.exp.net.netty.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 2017/4/10.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(ctx.channel().id().toString());
        String body = (String) msg;
        logger.info(ctx.channel().remoteAddress().toString());
        logger.info("server received data :" + body);
        ctx.writeAndFlush("response.\n");//写回数据，
    }
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("channelReadComplete");
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) //flush掉所有写回的数据
//                .addListener(ChannelFutureListener.CLOSE); //当flush完成后关闭channel
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        logger.error(cause.getLocalizedMessage(), cause);
        System.out.println("exceptionCaught");
        ctx.close();//出现异常时关闭channel
    }

    // NettyServer中设置的 .addLast(new IdleStateHandler(12, 12, 10, TimeUnit.SECONDS)) 值，触发
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    System.out.println("READER_IDLE 读超时");
                    ctx.disconnect();
                    break;
                case WRITER_IDLE:
                    System.out.println("WRITER_IDLE 写超时");
                    ctx.disconnect();
                    break;
                case ALL_IDLE:
                    System.out.println("ALL_IDLE 总超时");
                    ctx.disconnect();
                    break;
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 释放掉该context相关资源
        super.channelUnregistered(ctx);
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("channelRegistered");
    }
}
