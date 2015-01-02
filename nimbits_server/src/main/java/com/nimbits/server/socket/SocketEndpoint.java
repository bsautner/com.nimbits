package com.nimbits.server.socket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.auth.AuthService;
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
public class SocketEndpoint extends WebSocketServlet {

    @Autowired
    private AuthService authService;

    @Autowired
    private ConnectedClients connectedClients;


    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        System.out.println(Parameters.test);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        try {
            getServletContext().getNamedDispatcher("default").forward(request,
                    response);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request,
                                        String protocol) {

        System.out.println("CONNECTION INCOMING!");



        String ids = request.getParameter(Parameters.points.getText());

        String authToken = request.getParameter(Parameters.authToken.getText());

        List<EmailAddress> users = authService.getCurrentUser(request);

        if (users.isEmpty()) {
            throw new SecurityException("Session not found, did you POST to the session api first?");
        }
        else {
            String email = users.get(0).getValue();
            System.out.println("Connection : " + authToken);
            List<String> points;
            if (!Utils.isEmptyString(ids)) {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<List<String>>() {
                }.getType();
                points = gson.fromJson(ids, type);

            } else {
                points = Collections.emptyList();
            }
            List<String> fixed = new ArrayList<>(points.size());
            for (String p : points) {
                if (Utils.isNotEmpty(p)) {
                    if (!p.startsWith(email)) {
                        fixed.add(email + "/" + p);
                    } else {
                        fixed.add(p);
                    }
                }
            }


            System.out.println("Connection from : " + email);
            System.out.println("Connection auth : " + authToken);
            System.out.println("Connection ids : " + ids);


            for (String s : fixed) {
                System.out.println("Connection id : " + s);
            }

            SocketClient client = new SocketClient(users.get(0), fixed, authToken);

            connectedClients.add(client);
            return client;
        }

    }


}