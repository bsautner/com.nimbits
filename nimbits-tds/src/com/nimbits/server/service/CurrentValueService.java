/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.service;

import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.service.impl.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;


public class CurrentValueService extends HttpServlet {

    private static final Logger log = Logger.getLogger(CurrentValueService.class.getName());
    private static final long serialVersionUID = 1L;
    //private final static Gson gson = GsonFactory.getInstance();

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {


        final String pointNameParam = req.getParameter(Const.Params.PARAM_POINT);
        final String valueStr = req.getParameter(Const.PARAM_VALUE);
        final String json = req.getParameter(Const.Params.PARAM_JSON);
        final String note = req.getParameter(Const.Params.PARAM_NOTE);
        final String lat = req.getParameter(Const.Params.PARAM_LAT);
        final String lng = req.getParameter(Const.Params.PARAM_LNG);
        final String timestampStr = req.getParameter(Const.Params.PARAM_TIMESTAMP);
        final String jsonData = req.getParameter(Const.PARAM_DATA);

        //   final PrintWriter out = resp.getWriter();

        try {
//
//
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            final Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            final Point point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());


            if (point == null) {

                throw new NimbitsException(Const.ERROR_POINT_NOT_FOUND);
            } else {


                if (!u.isRestricted()) {
                    final Value v;

                    if (!Utils.isEmptyString(json)) {
                        final Value vx = GsonFactory.getInstance().fromJson(json, ValueModel.class);

                        v = ValueModelFactory.createValueModel(vx.getLatitude(), vx.getLongitude(), vx.getNumberValue(), vx.getTimestamp(),
                                point.getUUID(), vx.getNote(), vx.getData());
                    } else {
                        final double latitude = getDoubleFromParam(lat);
                        final double longitude = getDoubleFromParam(lng);
                        final double value = getDoubleFromParam(valueStr);
                        final Date timestamp = (timestampStr != null) ? (new Date(Long.parseLong(timestampStr))) : new Date();
                        v = ValueModelFactory.createValueModel(latitude, longitude, value, timestamp, point.getUUID(), note, jsonData);
                    }

                    Value result = RecordedValueServiceFactory.getInstance().recordValue(u, point, v, false);
                    final PrintWriter out = resp.getWriter();
                    String j = GsonFactory.getInstance().toJson(result);
                    out.print(j);

                } else {
                    throw new NimbitsException("Could not record value, User does not have permission");
                }
            }
        } catch (final NimbitsException e) {
            if (!e.getMessage().equals(Const.ERROR_POINT_NOT_FOUND)) {
                log.severe("Current Value Service Error");
                log.severe(e.getMessage());
            }
        }


    }

    private double getDoubleFromParam(final String valueStr) {
        double retVal;
        try {
            retVal = (valueStr != null) ? Double.valueOf(valueStr) : 0;
        } catch (NumberFormatException e) {
            retVal = 0;
        }
        return retVal;
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {


        final String pointNameParam = req.getParameter(Const.Params.PARAM_POINT);
        final String uuid = req.getParameter(Const.PARAM_UUID);
        final String formatParam = req.getParameter(Const.Params.PARAM_FORMAT);
        final String valueStr = req.getParameter(Const.PARAM_VALUE);
        final String json = req.getParameter(Const.Params.PARAM_JSON);
        final String note = req.getParameter(Const.Params.PARAM_NOTE);
        final String lat = req.getParameter(Const.Params.PARAM_LAT);
        final String lng = req.getParameter(Const.Params.PARAM_LNG);
        final String jsonData = req.getParameter(Const.PARAM_DATA);


        Value nv = null;

        final PrintWriter out = resp.getWriter();

        try {
            Common.addResponseHeaders(resp, ExportType.plain);
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

            final String format = (Utils.isEmptyString(formatParam)) ? Const.WORD_DOUBLE : formatParam;

            if (format.equals(Const.Params.PARAM_JSON) && !Utils.isEmptyString(json)) {
                nv = GsonFactory.getInstance().fromJson(json, ValueModel.class);
            } else if (format.equals(Const.WORD_DOUBLE) && !Utils.isEmptyString(valueStr)) {
                nv = ValueModelFactory.createValueModel(valueStr, note, lat, lng, jsonData);
            }
         out.println(processRequest(pointNameParam, uuid, format, nv, u));

        } catch (NimbitsException e) {
            out.println(e.getMessage());
            log.severe("Current Value Service Error");
            log.severe(e.getMessage());

        }

    }

    private static String processRequest(
            final String pointNameParam,
            final String uuid,
            final String format,
            final Value nv,
            final User u) throws NimbitsException {
        final Point p;

        String result = null;

        if (!Utils.isEmptyString(uuid)) {
            p = PointServiceFactory.getInstance().getPointByUUID(uuid);

        } else if (!Utils.isEmptyString(pointNameParam)) {
            EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            p = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

        } else {

            throw new NimbitsException(Const.ERROR_POINT_NOT_FOUND);
        }

        if (p != null) {
            final Value value;
            Entity e = EntityServiceFactory.getInstance().getEntityByUUID(p.getUUID());

            if ((u == null || u.isRestricted()) && ! e.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                throw new NimbitsException(Const.RESPONSE_PROTECTED_POINT);
            } else {
                if (nv != null && (u != null && !u.isRestricted())) {
                    // record the value, but not if this is a public
                    // request
                    final Value newValue = ValueModelFactory.createValueModel(
                            nv.getLatitude(), nv.getLongitude(), nv.getNumberValue(),
                            nv.getTimestamp(), p.getUUID(), nv.getNote(), nv.getData());


                    value = RecordedValueServiceFactory.getInstance().recordValue(u, p, newValue, false);
                } else {
                    value = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                }
                if (format.equals(Const.Params.PARAM_JSON)) {
                    result = GsonFactory.getInstance().toJson(value);
                } else {
                    result = String.valueOf(value.getNumberValue());
                }
            }
        }
        return result;
    }


}
