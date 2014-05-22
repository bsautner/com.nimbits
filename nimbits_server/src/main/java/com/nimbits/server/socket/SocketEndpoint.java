package com.nimbits.server.socket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketEndpoint extends WebSocketServlet {



    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getNamedDispatcher("default").forward(request,
                response);
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request,
                                        String protocol) {

       System.out.println("CONNECTION INCOMING!");

        String email = request.getParameter(Parameters.email.toString());
        String cid = request.getParameter(Parameters.cid.toString());
        String ids = request.getParameter(Parameters.points.toString());

        List<String> points;
        if (!Utils.isEmptyString(ids)) {
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<List<String[]>>() {}.getType();
             points = gson.fromJson(ids ,type );

        }
        else {
            points = Collections.emptyList();
        }
        List<String> fixed = new ArrayList<>(points.size());
        for (String p : points) {
            if (! p.startsWith(email) ) {
                fixed.add(email + "/" + p);
            }
            else {
                fixed.add(p);
            }
        }

        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);

        System.out.println("Connection from : "+ email);
        System.out.println("Connection cid : "+ cid);
        System.out.println("Connection ids : "+ ids);
        for (String s : fixed) {
            System.out.println("Connection id : "+ s);
        }
        return new SocketClient(emailAddress, fixed, cid);
    }


}