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
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.ApplicationListener;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.TaskServiceFactory;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


@Deprecated

public class ApiServlet extends HttpServlet {

    protected static User user;
    private static Map<Parameters, String> paramMap;

    protected static Location location;
    private static final String POST = "POST";
    protected EntityService entityService;
    protected ValueService valueService;
    protected NimbitsEngine engine;
    public TaskService taskService;

    protected static boolean okToReport(final User u, final Entity c) {

        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || (u != null && !u.isRestricted());
    }



    public void doInit(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type) {

        try {
            engine = (NimbitsEngine) getServletContext().getAttribute("engine");
            taskService = (TaskService) getServletContext().getAttribute("task");
        } catch (Exception e) {
            engine = null;
            taskService = null;
        }

        if (engine == null) {
            engine = ApplicationListener.createEngine();
            taskService = ApplicationListener.getTaskService(engine);

        }


        entityService = EntityServiceFactory.getInstance(engine);
        user = AuthenticationServiceFactory.getInstance(engine).getHttpRequestUser(req).get(0);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
        getGPS(req);
        buildParamMap(req);
        addResponseHeaders(resp, type);

    }

    private void buildParamMap(HttpServletRequest req) {
        paramMap = new EnumMap<Parameters, String>(Parameters.class);

        final Parameters[] items = {
                Parameters.point,
                Parameters.type,
                Parameters.action,
                Parameters.value,
                Parameters.json,
                Parameters.note,
                Parameters.lat,
                Parameters.lng,
                Parameters.timestamp,
                Parameters.data,
                Parameters.uuid,
                Parameters.format,
                Parameters.name,
                Parameters.points,
                Parameters.count,
                Parameters.autoscale,
                Parameters.category,
                Parameters.key,
                Parameters.client,
                Parameters.parent,
                Parameters.description,
                Parameters.action,
                Parameters.id

        };


        for (final Parameters s : items) {
            String v = req.getParameter(s.getText());
            if (!Utils.isEmptyString(v)) {
                paramMap.put(s, v);

            }

        }
    }

    public static ClientType getClientType() {
        ClientType type;
        if (containsParam(Parameters.client)) {
            type = ClientType.get(getParam(Parameters.client));
            if (type == null) {
                type = ClientType.other;
            }

        } else {
            type = ClientType.other;
        }
        return type;
    }

    public static void addResponseHeaders(final HttpServletResponse resp, final ExportType type) {
        if (!type.equals(ExportType.unknown)) {
            resp.setContentType(type.getCode());
        }
        //    resp.setContentType("text/plain");
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");

    }

    public static Location getGPS(final HttpServletRequest req) {
        if (req != null) {
            final String gps = req.getHeader("X-AppEngine-CityLatLong");
            if (!Utils.isEmptyString(gps)) {
                location = LocationFactory.createLocation(gps);
            } else {
                location = LocationFactory.createLocation();
            }
        } else {
            location = LocationFactory.createLocation();
        }
        return location;
    }

    protected static String getParam(final Parameters param) {

        return paramMap.containsKey(param) ? paramMap.get(param) : null;
    }

    protected static boolean containsParam(final Parameters param) {

        return paramMap.containsKey(param) && !Utils.isEmptyString(paramMap.get(param));

    }

    public void setEngine(NimbitsEngine engine) {
        this.engine = engine;
        this.entityService = EntityServiceFactory.getInstance(engine);
        this.taskService = TaskServiceFactory.getServiceInstance(engine);
        this.valueService = ValueServiceFactory.getInstance(engine, taskService);
    }
}
