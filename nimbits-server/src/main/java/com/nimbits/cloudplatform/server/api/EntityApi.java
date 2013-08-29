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

package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;



public class EntityApi extends ApiBase {


    final Logger log = Logger.getLogger(EntityApi.class.getName());
    //TODO - update, delete, create all handled with post with json in message body

    public static final String SERVER_RESPONSE = "SERVER_RESPONSE";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String CREATING_ENTITY = "Creating Entity";
    private String json;



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException, ServletException {
        //super.doGet(req, resp);
        final PrintWriter out = resp.getWriter();
        setup(req, resp);

        if (user != null) {

            List<Entity> sample = getEntity(user, req, resp);
            if (! sample.isEmpty()) {

                    String outJson = GsonFactory.getInstance().toJson(sample.get(0));
                    out.print(outJson);
                    log.info(outJson);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.close();
                }
            }
        }




    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        setup(req, resp);

        json = req.getParameter(Parameters.json.getText());
        if (StringUtils.isEmpty(json)) {
            json = getContent(req);
        }

        List<Entity> entityList = null;
        Action action = Action.valueOf(req.getParameter(Parameters.action.getText()));
        if (action != null && user != null) {

            switch (action) {

                case delete:
                    deleteEntity(resp, req);
                    break;
                case create:
                    entityList = createEntity(resp);
                    break;
                case update:
                    entityList = updateEntity(resp);
                    break;
                case createmissing:
                    try {
                        entityList = addMissingEntity(resp);
                    }
                    catch (IllegalArgumentException ex) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        resp.setHeader("ERROR_DETAILS", ex.getMessage());
                    }

            }
            if (entityList != null && ! entityList.isEmpty()) {
                final PrintWriter out;

                    out = resp.getWriter();

                    String outJson = GsonFactory.getInstance().toJson(entityList.get(0));
                    out.print(outJson);

                    //resp.setStatus(HttpServletResponse.SC_OK);
                    out.close();

            }


        }

    }

    private List<Entity> createEntity(HttpServletResponse resp)  {


        if (!StringUtils.isEmpty(json)) {
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {
                List<Entity> sample = EntityServiceImpl.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.addHeader("error details", "The entity you're trying to create already exists");
                    return Collections.emptyList();
                    // throw new IllegalArgumentException("The entity you're trying to create already exists");
                } else {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity();
                }


            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                return addUpdateUpscaledEntity();
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("error details", "invalid json");
            throw new IllegalArgumentException("Invalid JSON");

        }


    }

    private List<Entity> addUpdateUpscaledEntity( ) {
        Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
        Class cls = EntityHelper.getClass(entity.getEntityType());

        Object up = GsonFactory.getInstance().fromJson(json, cls);
        List<Entity> list = new ArrayList<Entity>(1);
        list.add((Entity) up);

        return EntityServiceImpl.addUpdateEntity(user, list);
    }

    private List<Entity> updateEntity(HttpServletResponse resp)  {



        if (!StringUtils.isEmpty(json)) {
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {

                List<Entity> sample = EntityServiceImpl.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity();
                } else {
                    resp.addHeader("error details", "entity not found");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new IllegalArgumentException("Entity Not Found");
                }

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new IllegalArgumentException("Entity Not Found");
            }

        } else {
            resp.addHeader("error details", "invalid json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException("Invalid JSON");

        }

    }

    private List<Entity> addMissingEntity(HttpServletResponse resp)  {



        if (!StringUtils.isEmpty(json)) {
            log.info(json);
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getName().getValue())) {

                List<Entity> sample = EntityServiceImpl.getEntityByName(user, sampleEntity.getName(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {
                    resp.addHeader(SERVER_RESPONSE, ENTITY_ALREADY_EXISTS);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return sample;
                } else {
                    resp.addHeader(SERVER_RESPONSE, CREATING_ENTITY);
                    return addUpdateUpscaledEntity();
                    //return EntityServiceImpl.addUpdateSingleEntity(user, sampleEntity);

                }

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new IllegalArgumentException("Entity Not Found");
            }

        } else {
            resp.addHeader("error details", "invalid json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException("Invalid JSON");

        }

    }
    private void deleteEntity(HttpServletResponse resp, final HttpServletRequest req)  {


        EntityType entityType = EntityType.valueOf(req.getParameter(Parameters.type.getText()));
        List<Entity> sample = EntityServiceImpl.getEntityByKey(user, req.getParameter(Parameters.id.getText()), entityType);

        if (sample.isEmpty()) {
            resp.addHeader("error details", "entity not found");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } else {
            Entity e = sample.get(0);

            EntityServiceImpl.deleteEntity(user, sample);
            resp.setStatus(HttpServletResponse.SC_OK);


        }
    }


}
