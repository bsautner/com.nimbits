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

package com.nimbits.server.api;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.io.socket.SocketConnection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.socket.ConnectedClients;
import com.nimbits.server.socket.SocketClient;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import org.eclipse.jetty.websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;


/**
 * GET requests will open all outbound web sockets to their target service.  Web sockets will be opened when any
 * subscription is configured to relay data over any open sockets to this server.  This API will open sockets in advance which
 * can then send and receive bidirectional data.
 */

public class SocketApi extends ApiBase {

    @Autowired
    private SubscriptionService subscriptionService;


    @Autowired
    private ConnectedClients connectedClients;

    @Autowired
    private TaskService taskService;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userJson = req.getParameter(Parameters.user.getText());
        String pointJson = req.getParameter(Parameters.point.getText());
        String valueJson = req.getParameter(Parameters.json.getText());
        Gson gson = GsonFactory.getInstance(true);
        Value value = gson.fromJson(valueJson, Value.class);
        Point point = gson.fromJson(pointJson, PointModel.class);
        User user = gson.fromJson(userJson, UserModel.class);
        String token = req.getParameter(Parameters.token.getText());

        connectedClients.sendLiveEvents(entityDao, taskService, subscriptionService, user, point, value, token);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        User user = (User) req.getAttribute(Parameters.user.getText());
        List<Entity> list = subscriptionService.sendSocket(entityDao, user, Collections.<Point>emptyList());
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");

        sb.append("<h5>Opening Outbound Connections</h5>" +
                "<ul>");
        for (Entity entity : list) {
            sb.append("<li>" + entity.getName() + "</li>");
        }
        sb.append("</ul>");
        Table<EmailAddress, String, SocketClient> clientMap = connectedClients.clientMap;
        Table<User, Socket, SocketConnection> outboundMap = connectedClients.outboundMap;

        sb.append("<h5>Outbound Connections</h5>" +
                "<ul>");
        for (SocketConnection row : outboundMap.row(user).values()) {
            WebSocket.Connection connection = row.getConnection();
            String data = "{ping}";
            ByteBuffer payload = ByteBuffer.wrap(data.getBytes());


            try {
                connection.sendMessage(data);

                sb.append("<li>" + row.getServer().getBaseUrl().toString() + " connected:" + row.getConnection().isOpen() + "</li>");
            } catch (IOException e) {
                sb.append(e.getMessage());
                // e.printStackTrace(System.err);
            }

        }
        sb.append("</ul>");

        sb.append("<h5>Inbound Connections</h5><ul>");
        for (SocketClient row : clientMap.row(user.getEmail()).values()) {
            sb.append("<li>" + row.getSession() + " connected:" + row.getConnection().isOpen() + "</li>");
        }
        sb.append("</ul>");
        sb.append("</body></html>");
        writer.print(sb.toString());
        writer.close();

    }
}
