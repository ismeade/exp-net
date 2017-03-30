package com.ade.exp.mina.sshd;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by liyang on 2017/3/29.
 */
public class MyCommand implements Command, Runnable {

    private final static String IDENTIFIER_IN      = "mcs>";
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
        write("welcome " + IDENTIFIER_NEWLINE + IDENTIFIER_NEWLINE +
                "Command:" + IDENTIFIER_NEWLINE +
                "1.test : run test." + IDENTIFIER_NEWLINE +
                "2.exit/quit : Disconnected." + IDENTIFIER_NEWLINE + IDENTIFIER_NEWLINE +
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
//                    case 27: // Esc
//                        break;
//                    case 65: // up
//                    case 66: // down
//                    case 67: // right
//                    case 68: // left
//                        out.write(27);
//                        out.write(91);
//                        out.write(68);
//                        out.flush();
//                        break;
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
        String com = builder.toString().trim();
        if (com.length() > 0) {
            write(IDENTIFIER_NEWLINE);
            switch (com) {
                case "2":
                case "quit":
                case "exit":
                    return false;
                case "1":
                case "test":
                    test();
                    break;
                default:
//                    Process p = null;
//                    InputStream in = null;
//                    try {
//                        p = Runtime.getRuntime().exec("cmd.exe /c " + com);
//                        in = p.getInputStream();
//                        while (in.read() != -1) {
//                            out.write(in.read());
//                        }
//                        write(IDENTIFIER_NEWLINE);
//                    } catch (IOException e) {
//                        System.out.println(e.getLocalizedMessage());
//                    } finally {
//                        if (p != null) p.destroy();
//                        if (in != null) try {
//                            in.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    write(com + ": command not found");
                    break;
            }
            write(IDENTIFIER_NEWLINE + IDENTIFIER_IN);
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

    private void test() {
        try {
            out.write(91);
            for (int i = 0; i < 100; i++) {
                out.write(27);
                out.write(91);
                out.write(67);
            }
            out.write(93);
            out.flush();
            for (int i = 0; i < 101; i++) {
                out.write(27);
                out.write(91);
                out.write(68);
            }
            for (int i = 0; i < 100; i++) {
                TimeUnit.MILLISECONDS.sleep(100);
                out.write(42);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}
