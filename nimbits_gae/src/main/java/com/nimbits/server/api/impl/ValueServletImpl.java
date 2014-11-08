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

package com.nimbits.server.api.impl;

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;
import com.nimbits.client.model.value.impl.ValueDataModel;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


@Deprecated
public class ValueServletImpl extends ApiServlet {
    final private static Logger log = Logger.getLogger(ValueServletImpl.class.getName());
    final private static String WORD_DOUBLE = "double";


    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        doInit(req, resp, ExportType.plain);

        log.info("recording post");

        if (user != null && !user.isRestricted()) {
            String name = getParam(Parameters.point);
            if (Utils.isEmptyString(name)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setHeader("Error", "Missing point name - you are using a deprecated service, please use /v2/value - see manual");
                return;
            }
            final EntityName pointName = CommonFactory.createName(getParam(Parameters.point), EntityType.point);
            final List<Entity> points = entityService.getEntityByName(user, pointName, EntityType.point);

            if (points.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Point Not Found");

            } else {
                final Value v;
                final Point point = (Point) points.get(0);
                if (Utils.isEmptyString(getParam(Parameters.json))) {
                    v = createValueFromRequest(point.inferLocation());
                } else {
                    final Value vx = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);
                    Location l = vx.getLocation();
//                    log.info(point.getName().getValue() + " " + point.inferLocation());
                    if (point.inferLocation() && vx.getLocation().isEmpty()) {
                        l = location;
                    }
//                    log.info(location.toString());
                    v = ValueFactory.createValueModel(l, vx.getDoubleValue(), vx.getTimestamp(),
                            vx.getData(), AlertType.OK);
                }


                //reportLocation(point, location);

                final PrintWriter out;
                try {
                    final Value result = valueService.recordValue(user, point, v, false);
                    out = resp.getWriter();
                    final String j = GsonFactory.getInstance().toJson(result);
                    out.print(j);
                } catch (IllegalArgumentException ex) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                } catch (IOException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                } catch (ValueException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }


            }
            resp.setStatus(Const.HTTP_STATUS_OK);
        } else {
            resp.setStatus(Const.HTTP_STATUS_UNAUTHORISED);
        }

    }


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            doInit(req, resp, ExportType.plain);
            final PrintWriter out = resp.getWriter();
            Value nv = null;
            final String format = getParam(Parameters.format) == null ? WORD_DOUBLE : getParam(Parameters.format);

            if (format.equals(Parameters.json.getText()) && !Utils.isEmptyString(getParam(Parameters.json))) {
                nv = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);
            } else if (format.equals(WORD_DOUBLE) && !Utils.isEmptyString(getParam(Parameters.value))) {

                nv = createValueFromRequest(false);

            }
            out.print(processRequest(req, getParam(Parameters.point), getParam(Parameters.uuid), format, nv, user));
            out.close();
            resp.setStatus(Const.HTTP_STATUS_OK);


        } catch (Exception e) {

        }
    }

    private static Value createValueFromRequest(boolean inferLocation) {
        Value nv;
        final double latitude = getDoubleFromParam(getParam(Parameters.lat));
        final double longitude = getDoubleFromParam(getParam(Parameters.lng));
        final double value = getDoubleFromParam(getParam(Parameters.value));
        final String data = getParam(Parameters.data);
        final ValueData vd = ValueDataModel.getInstance(SimpleValue.getInstance(data));// ValueFactory.createValueData(data);
        final Date timestamp = getParam(Parameters.timestamp) != null ? new Date(Long.parseLong(getParam(Parameters.timestamp))) : new Date();
        Location location1 = LocationFactory.createLocation(latitude, longitude);
        if (inferLocation && location1.isEmpty()) {
            location1 = location;
        }


        nv = ValueFactory.createValueModel(location1, value, timestamp, vd, AlertType.OK);
        return nv;
    }

    private static double getDoubleFromParam(final String valueStr) {
        double retVal;
        try {
            retVal = valueStr != null ? Double.valueOf(valueStr) : 0;
        } catch (NumberFormatException e) {
            retVal = 0;
        }
        return retVal;
    }

    public String processRequest(
            final HttpServletRequest req,
            final String pointNameParam,
            final String uuid,
            final String format,
            final Value nv,
            final User u) throws Exception {

        final List<Entity> result;
        if (!Utils.isEmptyString(uuid)) {
            result = entityService.getEntityByKey(u, uuid, EntityType.point);
        } else if (!Utils.isEmptyString(pointNameParam)) {
            final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);

            result = entityService.getEntityByName(u, pointName, EntityType.point);
        } else {
            throw new Exception(UserMessages.ERROR_POINT_NOT_FOUND);
        }

        if (result.isEmpty()) {
            throw new Exception(UserMessages.ERROR_POINT_NOT_FOUND);
        } else {

            final Entity p = result.get(0);
            if ((u == null || u.isRestricted()) && !p.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                throw new Exception(UserMessages.RESPONSE_PROTECTED_POINT);
            }
            Value value = null;
            if (nv != null && u != null && !u.isRestricted()) {
                // record the value, but not if this is a public
                // request
                final Value newValue = ValueFactory.createValueModel(
                        nv.getLocation(), nv.getDoubleValue(),
                        nv.getTimestamp(), nv.getData(), AlertType.OK);


                value = valueService.recordValue(u, p, newValue, false);
                if (nv.getLocation().isEmpty()) {
                    //reportLocation(p, location);
                } else {
                    //  reportLocation(p,nv.getLocation());
                }
            } else {
                List<Value> values = valueService.getCurrentValue(p);
                if (!values.isEmpty()) {
                    value = values.get(0);
                }
            }
            String r = value != null ? format.equals(Parameters.json.getText()) ? GsonFactory.getInstance().toJson(value) : String.valueOf(value.getDoubleValue()) : "";

            if (containsParam(Parameters.client) && getParam(Parameters.client).equals(ClientType.arduino.getCode())) {
                r = Const.CONST_ARDUINO_DATA_SEPARATOR + r + Const.CONST_ARDUINO_DATA_SEPARATOR;
            }

            return r;

        }

    }


}
