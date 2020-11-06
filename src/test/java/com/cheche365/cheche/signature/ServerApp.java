
package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.api.ServletPreSignRequest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class ServerApp {

    private static Server server;

    public static void start(int port) {

        server = new Server(port);
        new Thread() {
            @Override
            public void run() {
                try {
                    ServletHandler handler = new ServletHandler();
                    server.setHandler(handler);
                    handler.addServletWithMapping(OrdersResource.class, "/orders/*");
                    server.start();
                    server.join();
                } catch (Exception ex) {
                    System.out.println("服务器启动失败");
                }
            }
        }.start();
    }

    public static void stop() {

        server.setStopTimeout(1000L);
        new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception ex) {
                    System.out.println("服务器关闭失败");
                }
            }
        }.start();
    }

    public static class OrdersResource extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            ServletPreSignRequest signRequest = new ServletPreSignRequest(new BodyReadRequestWrapper(request));
            Parameters parameters = new Parameters().readRequest(signRequest);
            Secrets secrets = new Secrets().appSecret(SignatureTest.HMAC_APP_SECRET);

            try {
                if(!APISignature.verify(signRequest, parameters, secrets)){
                    throw new IllegalArgumentException("签名校验失败");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("签名校验失败!" + e.getMessage());
            }
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from HelloServlet</h1>");
        }
    }
}

