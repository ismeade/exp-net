package com.ade.exp.mina.sshd;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.*;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by liyang on 2017/3/29.
 */
public class SshServerExp {

    public static void main(String[] args) {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2222);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("hostkey.ser")));
        // 测试 一律通过
        sshd.setPasswordAuthenticator((s, s2, serverSession) -> "guest".equals(s));
        sshd.setPublickeyAuthenticator((s, publicKey, serverSession) -> "guest".equals(s));

        sshd.setShellFactory(MyCommand::new);
//        sshd.setCommandFactory(s -> new MyCommand());
        try {
            sshd.start();
            TimeUnit.SECONDS.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
