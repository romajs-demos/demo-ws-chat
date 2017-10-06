package com.romajs.ws.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat")
public class ChatServerEndpoint {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @OnOpen
    public void onOpen(Session session) {
        logger.debug("connected from {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.debug("received from {}: \"{}\"", session.getId(), message);
        switch (message) {
            case "exit":
                try {
                    session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Exit"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                for (Session s : session.getOpenSessions()) {
                    if (s.isOpen() && !s.getId().equals(session.getId())) {
                        logger.debug("sending from {}: \"{}\", to {}", session.getId(), message, s.getId());
                        s.getBasicRemote().sendText(message);
                    }
                }
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.debug("closed from {}: \"{}\"", session.getId(), closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("error from {}: \"{}\"", session.getId(), throwable.getMessage(), throwable);
    }
}