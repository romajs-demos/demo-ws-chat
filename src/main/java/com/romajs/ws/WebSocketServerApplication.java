package com.romajs.ws;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.reflections.Reflections;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Set;

public class WebSocketServerApplication {

    public static final String PROTOCOL = "ws";
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 8080;
    public static final String ROOT_PATH = "/";
    public static final String ENDPOINT_PACKAGE = "com.romajs.ws.server";

    private Server server;

    public WebSocketServerApplication() {
        Set<Class<?>> endpointClasses = new Reflections(ENDPOINT_PACKAGE).getTypesAnnotatedWith(ServerEndpoint.class);
        server = new Server(new InetSocketAddress(HOSTNAME, PORT));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(ROOT_PATH);
        server.setHandler(context);

        try {
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
            for (Class<?> aClass : endpointClasses) {
                wscontainer.addEndpoint(aClass);
            }
        } catch (ServletException | DeploymentException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        WebSocketServerApplication wsApplication = new WebSocketServerApplication();
        try {
            wsApplication.start();
            Thread.sleep(100);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press any key to stop the server...");
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            wsApplication.stop();
        }
    }

    public static URI getURI(String endpoint) {
        String uri = String.format("%s:%d/%s/%s", HOSTNAME, PORT, ROOT_PATH, endpoint);
        while(uri.indexOf("//") > -1) {
            uri = uri.replaceAll("//", "/");
        }
        return URI.create(String.format("%s://%s", PROTOCOL, uri));
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
