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

package com.nimbits.client.io.socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.gson.GsonFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Opens a web socket connection to a nimbits server
 */

public class SocketConnection extends GsonFactory {

    private WebSocketClientFactory factory;
    private WebSocketClient client;
    private WebSocket.Connection connection;

    private Instance server;


    public SocketConnection(Instance aServer, final SocketListener listener) throws Exception {
        this.factory = new WebSocketClientFactory();
        this.factory.start();
        this.client = factory.newWebSocketClient();
        this.server = aServer;

        StringBuilder sb = new StringBuilder();

        String u;
        boolean usingCloud = server.getBaseUrl().getUrl().contains("nimbits.com");
        if (usingCloud) {
            u = Const.SOCKET_RELAY;
        } else {
            u = server.getBaseUrl().getUrl().replace("http://", "").replace("https://", "");
        }

        sb
                .append("ws://").append(u).append("/socket?")
                .append(Parameters.email + "=" + server.getAdminEmail().getValue())
                .append("&" + Parameters.format + "=" + "json")
                .append("&" + Parameters.token + "=" + server.getApiKey().getCode());


        if (usingCloud) {
            sb.append("&" + Parameters.forward + "=" + "http://" + server.getBaseUrl());

        }
        //System.out.println(sb.toString());

        //TODO - pass an array of point id's to limit the points this socket cares about

        connection = client.open(new URI(sb.toString()


        ), new WebSocket.OnTextMessage()

        {
            public void onOpen(WebSocket.Connection connection) {
                listener.onOpen(connection);
            }

            public void onClose(int closeCode, String message) {
                listener.onClose(closeCode, message);
            }

            public void onMessage(String data) {
                System.out.println("incoming raw data: " + data);

                try {
                    Gson gson = GsonFactory.getInstance(true);



                    Point result = gson.fromJson(data, PointModel.class);
                    listener.onNotify(result);
//                    if (result != null && ! result.isEmpty()) {
//                        if (result.get(0).getAction().equals(Action.notify)) {
//                            listener.onNotify(result.get(0));
//                        }
//                        else {
//                            listener.onUpdate(result.get(0));
//                        }
//                    }
                } catch (JsonSyntaxException e) {
                    System.out.println(e.getMessage());
                    //wasn't json array, not processing
                }
            }
        }).get(30, TimeUnit.SECONDS);

    }

    public void sendMessage(String message) throws IOException {
        connection.sendMessage(message);
    }


    public WebSocket.Connection getConnection() {
        return connection;
    }

    public Instance getServer() {
        return server;
    }
}
