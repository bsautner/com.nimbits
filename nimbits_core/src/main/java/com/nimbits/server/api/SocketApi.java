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
import javax.servlet.http.HttpServlet;
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
@Deprecated
public class SocketApi extends HttpServlet {


    private TaskService taskService;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }
}
