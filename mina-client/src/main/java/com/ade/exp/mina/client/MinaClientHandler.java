package com.ade.exp.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 *
 * Created by liyang on 17-2-24.
 */
public class MinaClientHandler extends IoHandlerAdapter {

    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("Open:" + session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println("Received:" + message);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("Close:" + session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        System.out.println("exception");
    }
}
