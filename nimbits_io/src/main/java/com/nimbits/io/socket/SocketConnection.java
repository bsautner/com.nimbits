package com.nimbits.io.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.SocketType;
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




    public SocketConnection(Server aServer, EmailAddress email, SocketType socketType, final SocketListener listener) throws Exception {
        this.factory = new WebSocketClientFactory();
        this.factory.start();
        this.client = factory.newWebSocketClient();
        this.server = aServer;



        String connectionid = UUID.randomUUID().toString();

        connection = client.open(new URI("ws://" + server.getUrl() + "/socket?" +
                Parameters.email + "=" + email.getValue() +
                "&" + Parameters.cid +  "=" + connectionid +
                "&" + Parameters.type + "=" + socketType.toString() +
                "&" + Parameters.apikey + "=" + server.getApiKey().getValue()


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
                System.out.println(data);

                try {
                    Gson gson = new GsonBuilder()
                            .setDateFormat(GSON_DATE_FORMAT)
                            .serializeNulls()

                            .registerTypeAdapter(Value.class, new ValueDeserializer())
                            .registerTypeAdapter(Point.class, new PointDeserializer())
                            .registerTypeAdapter(AccessKey.class, new AccessKeyDeserializer())
                            .registerTypeAdapter(Entity.class, new EntityDeserializer())
                            .registerTypeAdapter(User.class, new UserDeserializer())
                            .registerTypeAdapter(Date.class, new DateDeserializer())
                            .create();


                    final Type listType = new TypeToken<List<PointModel>>() { }.getType();
                    List<Point> result = gson.fromJson(data, listType);
                    if (result != null && ! result.isEmpty()) {
                        if (result.get(0).getAction().equals(Action.notify)) {
                            listener.onNotify(result.get(0));
                        }
                        else {
                            listener.onUpdate(result.get(0));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println(e.getMessage());
                    //wasn't json array, not processing
                }
            }
        }).get(5, TimeUnit.SECONDS);

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
