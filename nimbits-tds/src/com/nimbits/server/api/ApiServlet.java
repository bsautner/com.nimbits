/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api;

import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.admin.quota.QuotaFactory;
import com.nimbits.server.api.helper.LocationReportingHelperFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 12:58 PM
 */
public class ApiServlet extends HttpServlet {

    protected static User user;
    private static Map<Parameters, String> paramMap;
    protected final static Logger log = Logger.getLogger(ApiServlet.class.getName());
    protected static Location location;

    protected static boolean okToReport(final User u, final Entity c) {

        return c.getProtectionLevel().equals(ProtectionLevel.everyone) ||  (u != null && ! u.isRestricted());
    }
    protected static boolean okToRead(final User u, final Entity c) {

        return (u != null && c.isOwner(u));
    }

//    protected static void reportLocation(HttpServletRequest req, Entity entity) {
//       LocationReportingHelperFactory.getInstance().reportLocation(req, entity);
//    }
    protected static void reportLocation(Entity entity, Location location) {

        LocationReportingHelperFactory.getInstance().reportLocation(entity, location);
    }

    public static void doInit(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type) throws NimbitsException {

        user = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
        getGPS(req);
        if (user != null) {
            QuotaFactory.getInstance(user.getEmail()).incrementCounter();
            log.info(user.getKey());
            log.info("keys: " + user.getAccessKeys().size());
        }



        paramMap = new EnumMap<Parameters, String>(Parameters.class);

        final Parameters[] items = {
                Parameters.point,
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
            paramMap.put(s, req.getParameter(s.getText()));
        }



        addResponseHeaders(resp, type);


    }

    public static ClientType getClientType() {
        ClientType type;
        if (containsParam(Parameters.client)) {
            type = ClientType.get(getParam(Parameters.client));
            if (type == null) {
                type = ClientType.other;
            }

        }
        else {
            type = ClientType.other;
        }
        return type;
    }

    public static void addResponseHeaders(final HttpServletResponse resp, final ExportType type) {
        if (! type.equals(ExportType.unknown)) {
            resp.setContentType(type.getCode());
        }
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

    public static Location getGPS(final HttpServletRequest req) {
        if (req != null) {
        final String gps = req.getHeader("X-AppEngine-CityLatLong");
        if (! Utils.isEmptyString(gps)) {
            location = LocationFactory.createLocation(gps);
        }
        else {
            location = LocationFactory.createLocation();
        }
        }
        else {
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
