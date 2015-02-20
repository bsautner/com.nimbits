package com.nimbits.io.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.http.SessionDeserializer;
import com.nimbits.server.gson.*;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Opens a web socket connection to a nimbits server
 *
 */

public class SocketConnection  {
    private static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
    private WebSocketClientFactory factory;
    private WebSocketClient client;
    private WebSocket.Connection connection;

    private Server server;




    public SocketConnection(Server aServer, final SocketListener listener) throws Exception {
        this.factory = new WebSocketClientFactory();
        this.factory.start();
        this.client = factory.newWebSocketClient();
        this.server = aServer;

        StringBuilder sb = new StringBuilder();

        String connectionid = UUID.randomUUID().toString();

        String u;
        boolean usingCloud = server.getUrl().contains("nimbits.com") || server.getUrl().contains(":8085");
        if (usingCloud) {
            u = "192.168.1.21:8080";
        }
        else {
            u = server.getUrl();
        }

        sb
                .append("ws://").append(u).append("/socket?")
                .append(Parameters.email + "=" + server.getEmail().getValue())
                .append("&" + Parameters.cid + "=" + connectionid)
                .append("&" + Parameters.format + "=" + "json")
                .append("&" + Parameters.apikey + "=" + server.getAccessCode().getValue())
                .append("&" + Parameters.authToken + "=" + server.getAccessCode().getValue());

        if (usingCloud) {
            sb.append("&" + Parameters.forward + "=" + server.getUrl());

        }
        System.out.println(sb.toString());

        //TODO - pass an array of point id's to limit the points this socket cares about
        connection = client.open(new URI(sb.toString()


        ), new WebSocket.OnTextMessage()

        {
            public void onOpen(WebSocket.Connection connection)
            {
                listener.onOpen(connection);
            }

            public void onClose(int closeCode, String message)
            {
                listener.onClose(closeCode, message);
            }

            public void onMessage(String data)
            {
                System.out.println("incoming raw data: " + data);

                try {
                    Gson gson = new GsonBuilder()
                            .setDateFormat(GSON_DATE_FORMAT)
                            .serializeNulls()

                            .registerTypeAdapter(Value.class, new ValueDeserializer())
                            .registerTypeAdapter(Point.class, new PointDeserializer())
                            .registerTypeAdapter(AccessKey.class, new AccessKeyDeserializer())
                            .registerTypeAdapter(Entity.class, new EntityDeserializer())
                            .registerTypeAdapter(User.class, new SessionDeserializer())
                            .registerTypeAdapter(Date.class, new DateDeserializer())
                            .create();



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

    public Server getServer() {
        return server;
    }
}
