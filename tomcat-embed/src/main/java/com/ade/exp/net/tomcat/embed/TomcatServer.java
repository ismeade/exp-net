package com.ade.exp.net.tomcat.embed;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.File;

/**
 *
 * Created by liyang on 2017/3/28.
 */
public class TomcatServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Integer port;

    public TomcatServer(Integer port) {
        this.port = port;
    }

    public void startUp() throws LifecycleException {
        try {
            if (null == port || port <= 0) {
                port = 8080;
                logger.info("tomcat server use default port:8080.");
            }
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(port);
            File base = new File(System.getProperty("user.dir"));
            Context rootCtx = tomcat.addContext("", base.getAbsolutePath());

            rootCtx.setDocBase(base.getPath());
            addServlet(rootCtx, "/", new DefaultServlet());

            tomcat.start();
            logger.info("http service is running. port: " + port);
            tomcat.getServer().await();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void addServlet(Context rootCtx, String page, HttpServlet httpServlet) {
        Tomcat.addServlet(rootCtx, httpServlet.getClass().getName(), httpServlet);
        rootCtx.addServletMapping(page, httpServlet.getClass().getName());
        logger.info("add Mapping : " + page + " -> " + httpServlet.getClass().getName());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new TomcatServer(8080).startUp();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

}
