package com.nimbits.server.service;



import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.*;
import com.nimbits.client.model.server.*;

import com.nimbits.server.dao.server.*;
import com.nimbits.server.gson.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/13/11
 * Time: 4:26 PM
 */
public class ServersServletImpl  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("not implemented");
     }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        String json = request.getParameter(Const.PARAM_JSON);
        PrintWriter out = response.getWriter();
        Server server = GsonFactory.getInstance().fromJson(json, ServerModel.class);
        Server retObj = null;
        try {
            retObj = ServerTransactionFactory.getInstance().addUpdateServer(server);
        } catch (NimbitsException e) {
            out.println(retObj);
        }
        String r = GsonFactory.getInstance().toJson(retObj);
        out.println(r);
        out.close();
    }
}
