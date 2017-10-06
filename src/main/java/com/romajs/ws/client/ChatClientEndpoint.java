package com.romajs.ws.client;

import com.romajs.ws.WebSocketServerApplication;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@ClientEndpoint
public class ChatClientEndpoint {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @OnOpen
    public void onOpen(Session session) {
        logger.debug("connected from {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.debug("received from {}: \"{}\"", session.getId(), message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.debug("closed from {}: \"{}\"", session.getId(), closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("error from {}: \"{}\"", session.getId(), throwable.getMessage(), throwable);
    }

    public static void main(String[] args) throws DeploymentException, IOException, InterruptedException {

        ClientManager client = ClientManager.createClient();
        Session session = client.connectToServer(ChatClientEndpoint.class, WebSocketServerApplication.getURI("/chat"));

        Thread readLineThread = new Thread(() -> {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            try {
                while((userInput = bufferRead.readLine()) != null && session.isOpen()) {
                    session.getBasicRemote().sendText(userInput);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        readLineThread.setDaemon(true);
        readLineThread.start();

        while(session.isOpen()) {
            Thread.sleep(100);
        }

        readLineThread.interrupt();
        session.close();
    }
}