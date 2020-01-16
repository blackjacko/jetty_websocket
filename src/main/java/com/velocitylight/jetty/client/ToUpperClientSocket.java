package com.velocitylight.jetty.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ToUpperClientSocket {

    private Session session;
    private String reply;

    CountDownLatch latch= new CountDownLatch(1);

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.latch.countDown(); // trigger latch
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {
        this.reply = message;
        System.out.println("Message received from server:" + message);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected to server");
        this.session=session;
        latch.countDown();
    }

    public void sendMessage(String str) {
        try {
            session.getRemote().sendString(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getReply() {
        return reply;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @OnWebSocketError
    public void onError(Throwable cause)
    {
        System.out.print("WebSocket Error: ");
        cause.printStackTrace(System.out);
    }
}
