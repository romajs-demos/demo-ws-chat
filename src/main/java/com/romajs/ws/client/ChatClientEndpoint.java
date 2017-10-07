package com.romajs.ws.client;

import com.romajs.ws.WebSocketServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        ChatClientEndpoint endpoint = new ChatClientEndpoint();
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

        Session session = webSocketContainer.connectToServer(endpoint, WebSocketServerApplication.getURI("/chat"));

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