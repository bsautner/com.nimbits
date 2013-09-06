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

import com.google.appengine.api.utils.SystemProperty;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransactionFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class ApiBase extends HttpServlet {
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String YOU_MUST_SUPPLY_AN_ENTITY_ID_OR_UUID = "You must supply an entity id or uuid";
    public static final String MESSAGE = "You must supply a valid email and secret key combo as a query string parameter, or " +
            " a valid API KEY";
    public static final String MESSAGE_CRED = "You did not provide credentials that can read that point, and the point is not public.";
    protected User user;
    protected String json;
    final static Logger log = Logger.getLogger(EntityApi.class.getName());




    protected void setup(HttpServletRequest req, HttpServletResponse resp, boolean readBody) throws ServletException  {
        log.info("api setup");
        Enumeration h = req.getHeaderNames();
        while (h.hasMoreElements()) {
            String header = (String) h.nextElement();

            log.info(header + "=" + req.getHeader(header));
        }

        Enumeration q = req.getParameterNames();
        while (q.hasMoreElements()) {
            String header = (String) q.nextElement();
            log.info(header + "=" + req.getParameter(header));
        }
        log.info(req.getQueryString());
        try {
            getUser(req, resp);
        }
        catch (IllegalArgumentException rx) {

            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, rx.getMessage());

        }
        if (user != null) {
            log.info(user.getEmail().getValue());
        }
        addHeaders(resp);
        if (readBody) {
            readJson(req);
        }
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
            user = UserTransactionFactory.getInstance().getHttpRequestUser(req);
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
            sample = EntityServiceImpl.getEntityByKey(user, id, entityType);
        }
        else if (!Utils.isEmptyString(uuid)) {
            response.append(" used uuid");
            sample = EntityServiceImpl.getEntityByUUID(user, uuid, entityType);
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
            log.severe(e.getMessage());
        }

    }
    protected void addHeaders(HttpServletResponse resp) {
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");
    }
}
