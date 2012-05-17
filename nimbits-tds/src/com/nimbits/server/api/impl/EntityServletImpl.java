package com.nimbits.server.api.impl;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.connection.ConnectionModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.file.FileModel;
import com.nimbits.client.model.intelligence.IntelligenceModel;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.xmpp.XmppResourceModel;
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

    private Class getClass(EntityType type) {
        switch (type) {


            case user:
                return UserModel.class;
            case point:
                return PointModel.class;
            case category:
                return CategoryModel.class;
            case file:
                return FileModel.class;
            case subscription:
                return SubscriptionModel.class;
            case userConnection:
                return ConnectionModel.class;
            case calculation:
                return CalculationModel.class;
            case intelligence:
                return IntelligenceModel.class;
            case feed:
                return null;
            case resource:
                return XmppResourceModel.class;
            case summary:
                return SummaryModel.class;
            case instance:
                return null;
            case accessKey:
                return AccessKeyModel.class;
            default:
                return null;
        }

    }


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
                        String json2 = getParam(Parameters.json);
                        if (!Utils.isEmptyString(json)) {

                            Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                            Class cls =  getClass(entity.getEntityType());
                            Object up = GsonFactory.getInstance().fromJson(json2,cls);
                            Entity r = null;
                            switch (action) {
                                case create:
                                    r =  EntityServiceFactory.getInstance().addUpdateEntity(user, (Entity) up);
                                    break;
                                case delete:
                                    EntityServiceFactory.getInstance().deleteEntity(user, (Entity) up) ;
                                    break;
                                case update:
                                    r =  EntityServiceFactory.getInstance().addUpdateEntity(user, (Entity) up);
                                    break;
                                default:
                                    break;

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

                if (! e.isEmpty() ) {
                    if (okToRead(user, e.get(0))) {
                        Entity r = e.get(0);
                        String json = GsonFactory.getInstance().toJson(r, r.getClass());
                        out.print(json);
                    }
                    else {
                        out.println("Could not display entity, access denied.");
                    }
                }
                else {
                    out.println("Could not find entity with the id provided.");
                }
            }


            out.close();

        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }
}
