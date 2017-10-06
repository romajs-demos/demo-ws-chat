package com.romajs.ws;

import org.glassfish.tyrus.server.Server;
import org.reflections.Reflections;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Set;

public class WebSocketServerApplication {

    public static final String PROTOCOL = "ws";
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 8025;
    public static final String ROOT_PATH = "/ws";
    public static final String ENDPOINT_PACKAGE = "com.romajs.ws.server";

    private Server server;

    public WebSocketServerApplication() {
        Set<Class<?>> endpointClasses = new Reflections(ENDPOINT_PACKAGE).getTypesAnnotatedWith(ServerEndpoint.class);
        server = new Server(HOSTNAME, PORT, ROOT_PATH, endpointClasses);
    }

    public static void main(String[] args) {
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
        String uri = String.format("%s:///%s:%d/%s/%s", PROTOCOL, HOSTNAME, PORT, ROOT_PATH, endpoint);
        return URI.create(uri.replaceAll("//", "/"));
    }

    public void start() throws DeploymentException {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}
