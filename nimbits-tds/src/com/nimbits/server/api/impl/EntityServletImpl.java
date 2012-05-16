package com.nimbits.server.api.impl;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class EntityServletImpl extends ApiServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try {
            final PrintWriter out = resp.getWriter();
            doInit(req, resp, ExportType.unknown);
            if (user != null && ! user.isRestricted()) {

                   if (containsParam(Parameters.action)) {
                       Action action = Action.get(getParam(Parameters.action));
                       if (action != null) {

                           String json = getParam(Parameters.json);
                           if (!Utils.isEmptyString(json)) {

                               Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                               Entity r = null;
                               switch (action) {
                                   case create:
                                      r =  EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                                   case delete:
                                       EntityServiceFactory.getInstance().deleteEntity(user, entity) ;
                                   case update:
                                      r =  EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

                               }
                               if (r != null) {
                                   String j = GsonFactory.getInstance().toJson(r);
                                   out.print(j);
                               }

                           }

                       }
                   }
                out.close();



            }
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doInit(req, resp, ExportType.unknown);
            final PrintWriter out = resp.getWriter();


            if (user != null && containsParam(Parameters.id)) {
                List<Entity> e = EntityServiceFactory.getInstance().findEntityByKey(user, getParam(Parameters.id));

                if (! e.isEmpty()) {
                    Entity r = e.get(0);
                    String json = GsonFactory.getInstance().toJson(r);
                    out.print(json);
                }
            }

             out.close();

        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }
}
