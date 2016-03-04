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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SocketEndpoint extends WebSocketServlet implements SocketEventListener {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ConnectedClients connectedClients;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ValueService valueService;

    private HttpServletRequest request;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        try {
            this.request = request;
            getServletContext().getNamedDispatcher("default").forward(request,
                    response);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request,
                                        String protocol) {


        String ids = request.getParameter(Parameters.points.getText());

        String authToken = request.getParameter(Parameters.token.getText());

        String forwardUrl = request.getParameter(Parameters.forward.getText());
        String emailParam = request.getParameter(Parameters.email.getText());

        User user;
        if (forwardUrl != null && emailParam != null && authToken != null) {
            //should only be called on the socket relay server
            //  forwardUrl = "http://localhost:8085";
//            Server server = ServerFactory.getInstance(UrlContainer.getInstance(forwardUrl),
//                    CommonFactory.createEmailAddress(emailParam), AccessToken.getInstance(authToken));
//            NimbitsClient client = NimbitsClientFactory.getInstance(server);
            Nimbits nimbits = new Nimbits.Builder().email(emailParam).token(authToken).instance(forwardUrl).create();
            user = nimbits.getMe(false);
           // users = Arrays.asList(user.getEmail());

            //notify cloud of socket
            //TODO nimbits.notifySocketConnection(forwardUrl, user);

        } else {

            user = userService.getHttpRequestUser(entityService, valueService, request);
            //users = authService.getCurrentUser(request);

        }


        if (user == null) {
            throw new SecurityException("Session not found, did you POST to the session api first?");
        } else {
            //String email = users.get(0).getValue();

            List<String> points;
            if (! StringUtils.isEmpty(ids)) {
                Gson gson =  GsonFactory.getInstance(true);
                Type type = new TypeToken<List<String>>() {
                }.getType();
                points = gson.fromJson(ids, type);

            } else {
                points = Collections.emptyList();
            }
            List<String> fixed = new ArrayList<>(points.size());
            for (String p : points) {
                if (StringUtils.isNotEmpty(p)) {
                    if (!p.startsWith(user.getEmail().getValue())) {
                        fixed.add(user.getEmail().getValue() + "/" + p);
                    } else {
                        fixed.add(p);
                    }
                }
            }


            SocketClient client = new SocketClient(this, user.getEmail(), fixed, authToken);

            connectedClients.add(client);
            return client;
        }

    }


    @Override
    public void onClose(int closeCode, String message, EmailAddress emailAddress, String authToken) {
        connectedClients.remove(emailAddress, authToken);
        userDao.deleteAuthToken(authToken);
        if (request != null) {
            request.getSession().invalidate();  //TODO notify cloud
        }
        System.out.println("socket event listener - on close.");
    }
}