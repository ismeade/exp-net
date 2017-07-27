package com.ade.exp.net.java;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 *
 * Created by liyang on 2017/7/6.
 */
public class HttpServerDemo {

    public void startUp() throws IOException {
        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);
        server.createContext("/api", exchange -> {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                byte[] bytes = new byte[10240];
                if (inputStream.read(bytes) == 10240) {
                    System.out.println("too lang");
                } else {
                    System.out.println(new String(bytes, "UTF-8"));
                }
            } else {
                OutputStream responseBody = exchange.getResponseBody();
                byte[] result = "only_post.".getBytes("UTF-8");
                exchange.sendResponseHeaders(HttpsURLConnection.HTTP_OK, result.length);
                responseBody.write(result);
                responseBody.close();
                exchange.close();
            }
        });
        server.setExecutor(Executors.newFixedThreadPool(1000));
        server.start();
        System.out.println("Server is listening on port 8080");
    }

    public static void main(String[] args) throws IOException {
        new HttpServerDemo().startUp();
    }

}
