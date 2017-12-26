package com.ade.exp.net.nio.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liyang on 17-12-22.
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {
                //
            }
        }
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(port);
        ExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(multiplexerTimeServer);

    }

}
