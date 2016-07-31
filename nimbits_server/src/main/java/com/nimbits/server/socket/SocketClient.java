/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.socket;

import com.nimbits.client.model.email.EmailAddress;
import org.eclipse.jetty.websocket.WebSocket;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class SocketClient implements WebSocket.OnTextMessage {
    private Connection connection;
    private EmailAddress email;
    private final List<String> points;
    private final SocketEventListener listener;


    private final String session;
    private Date st;


    public SocketClient(SocketEventListener listener, EmailAddress email, List<String> points, String session) {
        this.email = email;
        this.session = session;
        this.points = points;
        this.listener = listener;

    }

    @Override
    public void onClose(int closeCode, String message) {
        listener.onClose(closeCode, message, email, session); //TODO tell cloud about session closing?
        //System.out.println("life = " + (new Date().getTime() - st.getTime() / 1000));
        //System.out.println("closing: " + email + " " + session + " " + closeCode + " " + message);
    }

    public void sendMessage(String data) throws IOException {
        if (connection != null) {
            connection.sendMessage(data);
        }
    }

    @Override
    public void onMessage(String data) {
        // System.out.println("Received: " + authToken + ": " + data);


    }

    public boolean isOpen() {
        return connection != null && connection.isOpen();
    }

    @Override
    public void onOpen(Connection aConnection) {
        connection = aConnection;
        st = new Date();
        //connection.setMaxIdleTime(5000);
    }

    public Connection getConnection() {
        return connection;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public List<String> getPoints() {
        return points;
    }

    public String getSession() {
        return session;
    }


    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
