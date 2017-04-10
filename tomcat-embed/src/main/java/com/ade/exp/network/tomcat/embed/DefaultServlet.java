package com.ade.exp.network.tomcat.embed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * Created by liyang on 2017/3/28.
 */
public class DefaultServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter pw = response.getWriter()) {
            MethodManager methodManager = new MethodManager(HomePage.class);
            String uri = request.getRequestURI();
            String html = methodManager.run(uri, request);
            pw.append(html);
        }
	}



}
