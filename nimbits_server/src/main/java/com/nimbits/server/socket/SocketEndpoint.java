package com.nimbits.server.socket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessToken;
import com.nimbits.client.model.user.User;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.http.NimbitsClientFactory;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.transaction.user.dao.UserDao;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SocketEndpoint extends WebSocketServlet implements SocketEventListener {

    @Autowired
    private AuthService authService;

    @Autowired
    private ConnectedClients connectedClients;

    @Autowired
    private UserDao userDao;

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
        List<EmailAddress> users;

        if (forwardUrl != null && emailParam != null && authToken != null) {
            //should only be called on the socket relay server
            //  forwardUrl = "http://localhost:8085";
            Server server = ServerFactory.getInstance(UrlContainer.getInstance(forwardUrl),
                    CommonFactory.createEmailAddress(emailParam), AccessToken.getInstance(authToken));
            NimbitsClient client = NimbitsClientFactory.getInstance(server);
            User user = client.getSession();
            users = Arrays.asList(user.getEmail());

            //notify cloud of socket
            client.notifySocketConnection(forwardUrl, user);

        } else {

            users = authService.getCurrentUser(request);

        }


        if (users.isEmpty()) {
            throw new SecurityException("Session not found, did you POST to the session api first?");
        } else {
            String email = users.get(0).getValue();

            List<String> points;
            if (! StringUtils.isEmpty(ids)) {
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


            SocketClient client = new SocketClient(this, users.get(0), fixed, authToken);

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