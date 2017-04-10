package com.ade.exp.net.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by liyang on 17-2-24.
 */
public class MinaClientHandler extends IoHandlerAdapter {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.debug("Create [" + session + "]");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60000);
        logger.debug("Open [" + session + "]");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.debug("Close [" + session + "]");
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        logger.info("Received [" + session + "] " + message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.debug("Exception [" + session + "]", cause);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.debug("Idle [" + session + "]");
        session.close(false);
    }

}



