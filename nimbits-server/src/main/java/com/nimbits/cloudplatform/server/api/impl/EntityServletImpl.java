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

package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.shared.Utils;
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
                List<Entity> e = EntityServiceImpl.findEntityByKey(user, getParam(Parameters.id));

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
                            log.info(json);
                            Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                            Class cls =  EntityHelper.getClass(entity.getEntityType());
                            log.info(cls.getName());

                            Object up = GsonFactory.getInstance().fromJson(json2, cls);
                            List<Entity> r = null;
                            Entity e = (Entity)up;
                            validate(e);
                            List<Entity> entities = Arrays.asList(e);
                            switch (action) {
                                case create:

                                    r =  EntityServiceImpl.addUpdateEntity(user, entities);

                                    break;
                                case delete:
                                    EntityServiceImpl.deleteEntity(user, entities) ;
                                    break;
                                case update:
                                    r =  EntityServiceImpl.addUpdateEntity(user, entities);
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
