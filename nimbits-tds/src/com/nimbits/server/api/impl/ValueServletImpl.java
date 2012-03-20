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

package com.nimbits.server.api.impl;

import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.api.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;



public class ValueServletImpl extends ApiServlet {

    private static final long serialVersionUID = 1L;


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        init(req, resp, ExportType.plain);

        if (user != null && ! user.isRestricted()) {
            try {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(getParam(ApiParam.point), EntityType.point);
                final Entity e = EntityServiceFactory.getInstance().getEntityByName(user, pointName);
                final Point point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());


                if (point != null) {

                    final Value v;

                    if (!Utils.isEmptyString(getParam(ApiParam.json))) {
                        final Value vx = GsonFactory.getInstance().fromJson(getParam(ApiParam.json), ValueModel.class);

                        v = ValueModelFactory.createValueModel(vx.getLatitude(), vx.getLongitude(), vx.getNumberValue(), vx.getTimestamp(),
                                point.getUUID(), vx.getNote(), vx.getData());
                    } else {
                        final double latitude = getDoubleFromParam(getParam(ApiParam.lat));
                        final double longitude = getDoubleFromParam(getParam(ApiParam.lng));
                        final double value = getDoubleFromParam(getParam(ApiParam.value));
                        final Date timestamp = (getParam(ApiParam.timestamp) != null) ? (new Date(Long.parseLong(getParam(ApiParam.timestamp)))) : new Date();
                        v = ValueModelFactory.createValueModel(latitude, longitude, value, timestamp, point.getUUID(), getParam(ApiParam.note), getParam(ApiParam.json));
                    }

                    Value result = RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
                    final PrintWriter out = resp.getWriter();
                    String j = GsonFactory.getInstance().toJson(result);
                    out.print(j);

                } else {
                    FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(Const.ERROR_POINT_NOT_FOUND));
                }
            } catch (NimbitsException ex) {
                FeedServiceFactory.getInstance().postToFeed(user, ex);

            }
        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        init(req, resp, ExportType.plain);


        final PrintWriter out = resp.getWriter();
        Value nv = null;
        User u = null;
        try {

            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

            final String format = getParam(ApiParam.format)==null ? Const.WORD_DOUBLE : getParam(ApiParam.format);

            if (format.equals(Const.Params.PARAM_JSON) && !Utils.isEmptyString(getParam(ApiParam.json))) {
                nv = GsonFactory.getInstance().fromJson(getParam(ApiParam.json), ValueModel.class);
            } else if (format.equals(Const.WORD_DOUBLE) && !Utils.isEmptyString(getParam(ApiParam.value))) {
                nv = ValueModelFactory.createValueModel(
                        getParam(ApiParam.value),
                        getParam(ApiParam.note),
                        getParam(ApiParam.lat),
                        getParam(ApiParam.lng),
                        getParam(ApiParam.json));
            }
            out.println(processRequest(getParam(ApiParam.point), getParam(ApiParam.uuid), format, nv, u));

        } catch (NimbitsException e) {
            if (u != null) {
                FeedServiceFactory.getInstance().postToFeed(u, e);
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

    private static String processRequest(
            final String pointNameParam,
            final String uuid,
            final String format,
            final Value nv,
            final User u) throws NimbitsException {
        final Point p;

        String result;

        if (!Utils.isEmptyString(uuid)) {
            p = PointServiceFactory.getInstance().getPointByUUID(uuid);

        } else if (!Utils.isEmptyString(pointNameParam)) {
            EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            if (e != null) {
                p = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());
            }
            else {
                throw new NimbitsException(Const.ERROR_POINT_NOT_FOUND);
            }

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
        else {

            throw new NimbitsException(Const.ERROR_POINT_NOT_FOUND);
        }
        return result;
    }


}
