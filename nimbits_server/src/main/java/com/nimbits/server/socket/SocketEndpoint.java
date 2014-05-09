package com.nimbits.server.socket;


import com.nimbits.client.SocketType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        String email = request.getParameter(Parameters.email.toString());
        String cid = request.getParameter(Parameters.cid.toString());
        String type = request.getParameter(Parameters.type.toString());
        SocketType socketType =  SocketType.get(type);


        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);

        System.out.println("Connection from : "+ email);
        System.out.println("Connection cid : "+ cid);
        return new SocketClient(emailAddress, socketType, cid);
    }


}