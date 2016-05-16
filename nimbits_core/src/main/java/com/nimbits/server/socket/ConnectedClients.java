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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.nimbits.client.io.socket.SocketConnection;
import com.nimbits.client.io.socket.SocketListener;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class ConnectedClients {

    public Table<EmailAddress, String, SocketClient> clientMap;
    public Table<User, Socket, SocketConnection> outboundMap;

    private Gson gInstance;


    public ConnectedClients() {
        gInstance =  GsonFactory.getInstance(true);
        clientMap = HashBasedTable.create();
        outboundMap = HashBasedTable.create();
    }

    public void add(SocketClient socketClient) {
        SocketClient client = clientMap.get(socketClient.getEmail(), socketClient.getSession());

        if (client != null) {
            client.close();
        }

        clientMap.put(socketClient.getEmail(), socketClient.getSession(), socketClient);
    }

    public void remove(EmailAddress emailAddress, String authToken) {
        clientMap.remove(emailAddress, authToken);
    }

    public void sendMessage(User user, List<Point> points) throws IOException {


        Map<String, SocketClient> rows = clientMap.row(user.getEmail());
        for (SocketClient socketClient : rows.values()) {
            if (socketClient.isOpen()) {

                sendFormatedMessage(points, socketClient);
            }
        }

    }

    private void sendFormatedMessage(List<Point> points, SocketClient socketClient) throws IOException {
        for (Point point : points) {
            String json = gInstance.toJson(point);
            socketClient.sendMessage(json);
        }

    }

    public void sendLiveEvents(EntityDao entityDao, TaskService taskService, SubscriptionService subscriptionService, User user, Point point, Value value, String session) throws IOException {


        Map<String, SocketClient> rows = clientMap.row(user.getEmail());
        point.setValue(value);
        List<Point> points = Arrays.asList(point);

        for (SocketClient socketClient : rows.values()) {
            if (socketClient.getSession().equals(session) &&
                    socketClient.isOpen() &&
                    (socketClient.getPoints().isEmpty() || socketClient.getPoints().contains(point.getId())
                    )) {
                sendFormatedMessage(points, socketClient);

            }

        }

        for (Socket socket : outboundMap.row(user).keySet()) {

            sendOutbound(entityDao, subscriptionService, user, socket, points);

        }


    }

    public void sendOutbound(final EntityDao entityDao, final SubscriptionService subscriptionService, final User user, final Socket socket, List<Point> points) {


        UrlContainer INSTANCE_URL = UrlContainer.getInstance(socket.getTargetUrl() + socket.getTargetPath());

        String token =  socket.getTargetApiKey();
       // Server SERVER = ServerFactory.getInstance(INSTANCE_URL, user.getEmail(), token);
        Instance instance = new InstanceModel.Builder().adminEmail(user.getEmail()).password(token).baseUrl(INSTANCE_URL).create();


        try {
            final SocketConnection socketConnection;
            if (outboundMap.contains(user, socket)) {
                socketConnection = outboundMap.get(user, socket);
            } else {
                socketConnection = new SocketConnection(instance, new SocketListener() {


                    public void onOpen(Connection connection) {
                        //System.out.println("connected!");
                    }

                    public void onClose(int closeCode, String message) {
                        outboundMap.remove(user, socket);
                    }

                    @Override
                    public void onNotify(Point point) {
                        subscriptionService.processIncomingSocketValues(entityDao, user, point);
                        // System.out.println(point.getValue().getData());
                    }

                    @Override
                    public void onUpdate(Point point) {
                        subscriptionService.processIncomingSocketValues(entityDao, user, point);

                        // System.out.println(point.getValue().getData());
                    }
                });

                outboundMap.put(user, socket, socketConnection);
            }
            String json = gInstance.toJson(points);
            socketConnection.sendMessage(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void remove(User user, Socket entity) {
        outboundMap.remove(user, entity);

    }
}
