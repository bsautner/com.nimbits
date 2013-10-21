/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api.impl;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.EntityHelper;
import com.nimbits.shared.Utils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


@Service("entity")
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





    private void validate(Entity e)  {




    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        } catch (Exception e) {
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

                            Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                            Class cls =  EntityHelper.getClass(entity.getEntityType());


                            Object up = GsonFactory.getInstance().fromJson(json2, cls);
                            List<Entity> r = null;
                            Entity e = (Entity)up;
                            validate(e);
                            List<Entity> entities = Arrays.asList(e);
                            switch (action) {
                                case create:

                                    r =  entityService.addUpdateEntity(user, entities);

                                    break;
                                case delete:
                                    entityService.deleteEntity(user, entities) ;
                                    break;
                                case update:
                                    r =  entityService.addUpdateEntity(user, entities);
                                    break;
                                default:
                                    break;

                            }
                            if (r != null) {
                                String j = GsonFactory.getInstance().toJson(r.get(0));
                                out.print(j);
                            }

                        }

                    }
                }
                out.close();
                resp.setStatus(HttpServletResponse.SC_OK);


            }
        } catch (Exception e) {
            LogHelper.logException(this.getClass(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        }

    }
}
