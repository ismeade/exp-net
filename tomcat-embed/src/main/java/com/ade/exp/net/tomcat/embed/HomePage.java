package com.ade.exp.net.tomcat.embed;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by liyang on 2017/3/28.
 */
@Mapping(name="home")
public class HomePage {

    @Mapping(name="hello")
    public String hello(HttpServletRequest request) {
        return "<h1>this is home Page. hello: " + request.getParameter("name") + "</h1>";
    }

    @Mapping(name="world")
    public String world() {
        return "<h1>this is home Page. world</h1>";
    }

}
