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

import com.google.appengine.repackaged.com.google.common.base.Flag;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.ClientType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 12:58 PM
 */
@Service("apiServlet")
public class ApiServlet extends HttpServlet {

    protected static User user;
    private static Map<Parameters, String> paramMap;
    protected final static Logger log = Logger.getLogger(ApiServlet.class.getName());
    protected static Location location;
    private static final String POST = "POST";

    protected static boolean okToReport(final User u, final Entity c) {

        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || (u != null && !u.isRestricted());
    }

    protected static boolean okToRead(final User u, final Entity c) {

        return (u != null && c.isOwner(u));
    }

    protected boolean isPost(final HttpServletRequest req) {
        return req.getMethod().equals(POST);
    }


    //    protected static void reportLocation(HttpServletRequest req, Entity entity) {
//       LocationReportingHelperFactory.getInstance().reportLocation(req, entity);
//    }
//    protected  void reportLocation(Entity entity, Location location) {
//
//       // locationService.reportLocation(entity, location);
//    }
    protected String getContent(HttpServletRequest req)  {

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
    public void doInit(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type)   {
        // log.info(req.getQueryString());

        user = UserTransaction.getHttpRequestUser(req);

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
            String v =  req.getParameter(s.getText());
            if (! Utils.isEmptyString(v)) {
                paramMap.put(s,v);
                log.info(s.name() + "=" + v);
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


}
