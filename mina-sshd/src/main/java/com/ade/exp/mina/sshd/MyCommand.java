package com.ade.exp.mina.sshd;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.*;

/**
 *
 * Created by liyang on 2017/3/29.
 */
public class MyCommand implements Command, Runnable {

    private final static String IDENTIFIER_IN      = ">";
    private final static String IDENTIFIER_NEWLINE = "\r\n";

    private InputStream  in;
    private OutputStream out;
    private OutputStream err;
    private ExitCallback callback;
    private Environment  environment;
    private Thread thread;

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public OutputStream getErr() {
        return err;
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.err = err;
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        this.callback = callback;
    }

    @Override
    public void start(Environment env) throws IOException {
        environment = env;
        thread = new Thread(this, "EchoShell");
        thread.start();
        write("welcome" + IDENTIFIER_NEWLINE +
                "1.search" + IDENTIFIER_NEWLINE +
                "2.view" + IDENTIFIER_NEWLINE +
                "3.exit" + IDENTIFIER_NEWLINE + IDENTIFIER_NEWLINE +
                IDENTIFIER_IN);
    }

    @Override
    public void destroy() {
        thread.interrupt();
    }

    private StringBuilder builder = new StringBuilder();

    @Override
    public void run() {
        try {
            while (true) {
                int b = in.read();
                System.out.println(b);
                switch (b) {
                    case 8: // Backspace
                        break;
                    case 13: // Enter
                        if (!newLine()) {
                            return;
                        }
                        break;
                    case 27: // Esc
                        break;
                    case 65: // up
                    case 66: // down
                    case 67: // right
                    case 68: // left
                        break;
                    default:
                        input(b);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callback.onExit(0);
        }
    }

    private void write(int b) {
        try {
            out.write(b);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(String s) {
        try {
            out.write(s.getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean newLine() {
        if (builder.length() > 0) {
            if ("quit".equals(builder.toString())) {
                return false;
            }
            write(IDENTIFIER_NEWLINE + builder.toString() + IDENTIFIER_NEWLINE + IDENTIFIER_IN);
            builder.delete(0, builder.length());
        } else {
            write(IDENTIFIER_NEWLINE + IDENTIFIER_IN);
        }
        return true;
    }

    private void input(int b) {
        builder.append((char) b);
        write(b);
    }

}
