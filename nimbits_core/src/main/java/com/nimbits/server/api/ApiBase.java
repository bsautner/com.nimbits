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

package com.nimbits.server.api;


import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import org.apache.commons.lang3.StringUtils;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;


public class ApiBase extends HttpServlet {
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID = "You must supply an entity id or uuid";
    public static final String MESSAGE = "You must supply a valid email and secret key combo as a query string parameter, or " +
            " a valid API KEY";
    public static final String MESSAGE_CRED = "You did not provide credentials that can read that point, and the point is not public.";
    protected User user;
    protected String json;
    protected EntityService entityService;

    public PersistenceManagerFactory pmf;
    public NimbitsCache cache;
    public NimbitsEngine engine;
    public TaskService taskService;
    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void setup(HttpServletRequest req, HttpServletResponse resp, boolean readBody)   {
        setupEngine();
        entityService = EntityServiceFactory.getInstance(engine);

        try {
            getUser(req, resp);
        }
        catch (IllegalArgumentException rx) {

            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, rx.getMessage());

        }
        addHeaders(resp);
        if (readBody) {
            readJson(req);
        }
    }

    private void setupEngine() {
        engine = (NimbitsEngine) getServletContext().getAttribute("engine");
        taskService = (TaskService) getServletContext().getAttribute("task");
        pmf = engine.getPmf();
        cache = engine.getCache();


    }

    protected void completeResponse(HttpServletResponse resp, String respString) {
        PrintWriter out = null;

        try {

            out = resp.getWriter();
            out.print(respString);

            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        finally {
            if (out != null) {
                out.close();
            }
        }


    }
    protected void readJson(HttpServletRequest req) {
        json = req.getParameter(Parameters.json.getText());
        if (Utils.isEmptyString(json)) {
            json = getContent(req);
        }
    }


    protected String getContent(final HttpServletRequest req)  {

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
            }
            else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
    protected void getUser(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            user = AuthenticationServiceFactory.getInstance(engine).getHttpRequestUser(req);
        }
        catch (SecurityException ex) {
            user = null;

            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, MESSAGE);
        }
    }

    protected List<Entity> getEntity(final User user, final HttpServletRequest req, final HttpServletResponse resp) {
        List<Entity> sample;
        EntityType entityType;
        String id = req.getParameter(Parameters.id.getText());
        String uuid = req.getParameter(Parameters.uuid.getText());
        String type = req.getParameter(Parameters.type.getText());
        StringBuilder response = new StringBuilder();
        if (! StringUtils.isEmpty(type)) {
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
        else {
            entityType = EntityType.point;
        }
        if (!Utils.isEmptyString(id)) {
            response.append(" used id");
            sample = entityService.getEntityByKey(user, id, entityType);
        }
        else if (!Utils.isEmptyString(uuid)) {
            response.append(" used uuid");
            sample = entityService.getEntityByUUID(user, uuid, entityType);
        }
        else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID);
            return Collections.emptyList();
        }
        if (sample != null) {
            if (sample.isEmpty()) {

                response.append(ENTITY_NOT_FOUND);
                if (user == null) {
                    response.append(" User was null");
                }
                else {
                    response.append(GsonFactory.getInstance().toJson(user));
                }
                response.append(" uuid = ").append(uuid);
                response.append(" id = ").append(id);
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, response.toString());

                return Collections.emptyList();

            } else {
                if (user.isRestricted() && ! sample.get(0).getProtectionLevel().equals(ProtectionLevel.everyone)) {
                    sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, MESSAGE_CRED);
                    return Collections.emptyList();
                }
                else {
                    return sample;
                }

            }

        }
        else {
            return Collections.emptyList();
        }

    }
    protected void sendError(final HttpServletResponse resp, final int errorCode, final String message) {
        try {
            if (! resp.isCommitted()) {
                resp.sendError(errorCode, message);
                resp.setStatus(errorCode);
            }
        } catch (IOException e) {

        }

    }
    protected void addHeaders(HttpServletResponse resp) {
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");
    }
}
