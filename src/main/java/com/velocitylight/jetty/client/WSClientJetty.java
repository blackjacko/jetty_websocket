//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package com.velocitylight.jetty.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class WSClientJetty
{
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;
    private long timestamp;

    public WSClientJetty()
    {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
//    public void onConnect(Session session)
//    {
//        System.out.printf("Got connect: %s%n", session);
//        this.session = session;
//        try
//        {
//            Future<Void> fut;
//            fut = session.getRemote().sendStringByFuture("Hello");
//            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
//
//            fut = session.getRemote().sendStringByFuture("Thanks for the conversation.");
//            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
//        }
//        catch (Throwable t)
//        {
//            t.printStackTrace();
//        }
//    }


    public void onConnect(Session session) {
        System.out.println("Connected to server");
        this.session=session;
        this.timestamp = System.currentTimeMillis();
    }

    public void sendMessage(String str)
    {
        try {
            session.getRemote().sendString(str);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CountDownLatch getLatch() {
        return closeLatch;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Message received from server:" + message);
    }


    @OnWebSocketError
    public void onError(Throwable cause)
    {
        System.out.print("WebSocket Error: ");
        cause.printStackTrace(System.out);
    }
}