/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.xmpp.XmppResourceModel;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.shared.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Transactional
@Service("value")
public class EntityServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }

    }

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



    private void validate(Entity e) throws NimbitsException {

        if (e.getEntityType().equals(EntityType.point)) {
            Point p = (Point)e;
            if (p.getPointType().isSystem()) {
                throw new NimbitsException("Cannot create or modify system points using the entity service!");
            }
        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doInit(req, resp, ExportType.unknown);
            final PrintWriter out = resp.getWriter();


            if (user != null && containsParam(Parameters.id)) {
                List<Entity> e = entityService.findEntityByKey(user, getParam(Parameters.id));

                if (! e.isEmpty() ) {
                    if (okToRead(user, e.get(0))) {
                        Entity r = e.get(0);
                        String json = GsonFactory.getInstance().toJson(r, r.getClass());
                        out.print(json);
                    }
                    else {
                        out.println("Could not display entity, access denied.");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }
                else {
                    out.println("Could not find entity with the id provided.");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            out.close();

        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        }
    }



    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                            log.info(json);
                            Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                            Class cls =  getClass(entity.getEntityType());
                            log.info(cls.getName());

                            Object up = GsonFactory.getInstance().fromJson(json2, cls);
                            Entity r = null;
                            Entity e = (Entity)up;
                            validate(e);
                            switch (action) {
                                case create:

                                    r =  entityService.addUpdateEntity(user, e);
                                    log.info("created " + r.getKey());
                                    break;
                                case delete:
                                    entityService.deleteEntity(user, e) ;
                                    break;
                                case update:
                                    r =  entityService.addUpdateEntity(user, e);
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
                resp.setStatus(HttpServletResponse.SC_OK);


            }
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        }

    }
}
