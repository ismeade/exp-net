package com.ade.exp.mina.sshd;

import org.apache.sshd.common.util.OsUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.File;
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
//        sshd.setPublickeyAuthenticator((s, publicKey, serverSession) -> "guest".equals(s));
        //use file ~/.ssh/authorized_keys
        sshd.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));

//        sshd.setShellFactory(new ProcessShellFactory(OsUtils.WINDOWS_COMMAND));

        if (OsUtils.isUNIX()) {
            sshd.setShellFactory(new ProcessShellFactory(OsUtils.LINUX_COMMAND));
        } else {
            sshd.setShellFactory(MyCommand::new);
        }
        
//        sshd.setCommandFactory(s -> new MyCommand());
        try {
            sshd.start();
            TimeUnit.SECONDS.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
