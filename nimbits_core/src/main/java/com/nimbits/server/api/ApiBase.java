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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@Deprecated
public class ApiBase extends HttpServlet {
    public static final String YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID = "You must supply an entity name, id or uuid";
    public static final String MESSAGE_CRED = "You did not provide credentials that can read that point, and the point is not public.";


    final Logger logger = Logger.getLogger(ApiBase.class.getName());
    protected Gson gson =  GsonFactory.getInstance(true);


    @Autowired
    protected EntityDao entityDao;

    @Autowired
    protected EntityService entityService;

    @Autowired
    protected ValueService valueService;

    @Autowired
    protected BlobStore blobStore;

    @Autowired
    protected DataProcessor dataProcessor;



    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }



    protected void completeResponse(HttpServletResponse resp, String respString) {
        PrintWriter out = null;
        try {

            out = resp.getWriter();
            out.print(respString);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (IOException e) {
            logger.severe(e.getMessage());
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }


    }


    protected String getContent(final HttpServletRequest req) {

        BufferedReader reader;
        try {
            reader = req.getReader();
            if (req.getContentLength() > 0) {
                StringBuilder jb = new StringBuilder(req.getContentLength());
                String line;
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }


                return jb.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }



    protected Entity getEntity(final HttpServletRequest req, final HttpServletResponse resp, final User user) {
        Entity sample = null;
        EntityType entityType = null;
        String id = req.getParameter(Parameters.id.getText());
        String uuid = req.getParameter(Parameters.uuid.getText());
        String type = req.getParameter(Parameters.type.getText());
        String entityTypeString = req.getParameter("entityType");

        String name = req.getParameter(Parameters.name.getText());

        StringBuilder response = new StringBuilder();


        if (StringUtils.isNotEmpty(entityTypeString)) {
            try {
                response.append(" getting entity with type: " + entityTypeString);

                entityType = EntityType.getName(entityTypeString);
                response.append(" found : " + entityType.toString());
            } catch (Exception e) {
                logger.severe("error getting enum " + e.getMessage());

            }

        }

        if (entityType == null) {
            response.append("entity type was null when looking up " + entityTypeString);
        }

        if (entityType == null && StringUtils.isNotEmpty(type)) {
            try {
                int t = Integer.valueOf(type);
                entityType = EntityType.get(t);
                if (entityType == null) {
                    entityType = EntityType.point;
                }


            } catch (NumberFormatException e) {
                entityType = EntityType.point;
            }
        }

        if (entityType == null) {
            entityType = EntityType.point;
        }


        if (StringUtils.isNotEmpty(id)) {
            if (user != null && entityType.equals(EntityType.point) && !id.startsWith(user.getEmail().getValue())) {
                id = user.getEmail() + "/" + id;

            }
            response.append(" used entity id method");
            response.append(" getting ").append(id).append("");
            sample = entityDao.getEntityByKey(user, id, entityType).get();
        } else if (StringUtils.isNotEmpty(uuid)) {
            response.append(" used uuid method ");
            sample = entityDao.getEntityByUUID(user, uuid, entityType).get();
        } else if (StringUtils.isNotEmpty(name)) {
            response.append(" used name method ");
            EntityName entityName = CommonFactory.createName(name, entityType);
            Optional<Entity> optional = entityDao.getEntityByName(user, entityName, entityType);
            if (optional.isPresent()) {
                sample = optional.get();

            }
            else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Entity not found");
            }
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID);
            throw new RuntimeException(YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID);
        }

        if (sample != null) {
            if (user != null && !sample.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, MESSAGE_CRED);
                throw new RuntimeException(MESSAGE_CRED);
            } else {
                return sample;
            }
        }
        else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Entity not found");
            throw new RuntimeException("entity not found");
        }


    }

    protected void sendError(final HttpServletResponse resp, final int errorCode, final String message) {
        try {

            if (resp != null && !resp.isCommitted()) {
                resp.addHeader("Error-Response", message == null ? "unknown error" : message);
                resp.sendError(errorCode, message == null ? "unknown error" : message);
                resp.setStatus(errorCode);
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }

    }

    protected void initRequest(HttpServletRequest req, HttpServletResponse resp) {


        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");



    }


}
