/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * CRUD Operations on Entities
 */
@Deprecated
public class EntityApi extends ApiBase {


    //TODO - update, delete, create all handled with post with json in message body

    public static final String SERVER_RESPONSE = "SERVER_RESPONSE";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String CREATING_ENTITY = "Creating Entity";
    private final Gson gInstance =  GsonFactory.getInstance(true);


    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {


        initRequest(req, resp);

        User user = (User) req.getAttribute(Parameters.user.getText());
        if (user != null) {

            Entity sample = getEntity(req, resp, user);


            String outJson = gInstance.toJson(sample);
            completeResponse(resp, outJson);

        }

    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        initRequest(req, resp);
        User user = (User) req.getAttribute(Parameters.user.getText());


        String actionStr = req.getParameter(Parameters.action.getText());
        Action action = getAction(actionStr);
        String json = getJson(req);

        Entity entity = null;

        if (user != null) {

            switch (action) {

                case delete:
                    deleteEntity(resp, req, user);
                    break;
                case create:
                    entity = createEntity(req, resp, user, json);
                    break;
                case update:
                    entity = updateEntity(req, resp, user, json);
                    break;
                case createmissing:
                    try {
                        entity = addMissingEntity(req, resp, user, json);
                    } catch (IllegalArgumentException ex) {
                        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

                    }

            }
            if (entity != null) {


                String outJson = gInstance.toJson(entity);
                completeResponse(resp, outJson);

            }


        }

    }

    private String getJson(HttpServletRequest req) {
        String json = req.getParameter(Parameters.json.getText());
        if (StringUtils.isEmpty(json)) {
            json = getContent(req);
        }
        return json;
    }

    private Action getAction(String actionStr) {
        Action action;
        if (actionStr != null) {
            action = Action.get(actionStr);
            if (action == null || action.equals(Action.none)) {
                action = Action.create;

            }
        } else {
            action = Action.create;
        }
        return action;
    }

    private Entity createEntity(HttpServletRequest req, HttpServletResponse resp, final User user, final String json) {


        if (!StringUtils.isEmpty(json)) {


            Entity sampleEntity = null;
            try {
                Map jsonMap = gson.fromJson(json, Map.class);
                int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
                EntityType type = EntityType.get(t);
                sampleEntity = (Entity) gson.fromJson(json, type.getClz());

            } catch (JsonSyntaxException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {

                 Optional<Entity> optional = entityDao.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());

                if (optional.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.addHeader("error details", "The entity you're trying to create already exists");
                    throw new IllegalArgumentException("The entity you're trying to create already exists");
                    // throw new IllegalArgumentException("The entity you're trying to create already exists");
                } else {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity(req, user, json);
                }


            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                return addUpdateUpscaledEntity(req, user, json);
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("error details", "invalid json");
            throw new IllegalArgumentException("Invalid JSON");

        }


    }

    private Entity addUpdateUpscaledEntity(final HttpServletRequest req, final User user, final String json) {
        Map jsonMap = gson.fromJson(json, Map.class);
        int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
        EntityType type = EntityType.get(t);
        Entity sampleEntity = (Entity) gson.fromJson(json, type.getClz());


        return entityDao.addUpdateEntity(user, sampleEntity);
    }

    private Entity updateEntity(final HttpServletRequest req, HttpServletResponse resp, final User user, final String json) {
        if (!StringUtils.isEmpty(json)) {
            Map jsonMap = gson.fromJson(json, Map.class);
            int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
            EntityType type = EntityType.get(t);
            Entity sampleEntity = (Entity) gson.fromJson(json, type.getClz());


            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {


                 Optional<Entity> optional = entityDao.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());

                if (optional.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity(req, user, json);
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

    private Entity addMissingEntity(final HttpServletRequest req, HttpServletResponse resp, final User user, final String json) {


        if (!StringUtils.isEmpty(json)) {

            Map jsonMap = gson.fromJson(json, Map.class);
            int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
            EntityType type = EntityType.get(t);
            Entity sampleEntity = (Entity) gson.fromJson(json, type.getClz());
           // Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getName().getValue())) {

                Optional<Entity> optional = entityDao.getEntityByName(user, sampleEntity.getName(), sampleEntity.getEntityType());
                if ( optional.isPresent()) {
                    resp.addHeader(SERVER_RESPONSE, ENTITY_ALREADY_EXISTS);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return optional.get();
                } else {
                    resp.addHeader(SERVER_RESPONSE, CREATING_ENTITY);
                    return addUpdateUpscaledEntity(req, user, json);
                    //return entityServiceImpl.addUpdateSingleEntity(user, sampleEntity);

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

    private void deleteEntity(HttpServletResponse resp, final HttpServletRequest req, final User user) {
        String type = req.getParameter(Parameters.type.getText());
        String json = getJson(req);

        if (StringUtils.isEmpty(type)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "please provide a type param e.g type=point");
            return;
        }
        EntityType entityType = EntityType.valueOf(type);


        String id = req.getParameter(Parameters.id.getText());

        if (StringUtils.isEmpty(id)) {
            Map jsonMap = gson.fromJson(json, Map.class);
            int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
            EntityType tp = EntityType.get(t);
            Entity sampleEntity = (Entity) gson.fromJson(json, tp.getClz());

            if (sampleEntity != null) {
                id = sampleEntity.getKey();
            }
        }

        if (entityType == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "please provide a type param e.g type=point");
            return;
        }

        Optional<Entity> optional = entityDao.getEntityByKey(user, id, entityType);
        if (optional.isPresent()) {
            entityService.deleteEntity(user, optional.get());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }



    }


}
