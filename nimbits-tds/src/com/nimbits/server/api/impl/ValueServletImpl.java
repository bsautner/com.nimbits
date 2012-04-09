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

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.constants.Words;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModel;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.logging.LogHelper;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.value.RecordedValueServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;



public class ValueServletImpl extends ApiServlet {

    private static final long serialVersionUID = 1L;


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        try {
            processPost(req, resp);

        } catch (NimbitsException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, e);
            }
        }

    }
    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        try {
            processGet(req, resp);

        } catch (NimbitsException e) {

            LogHelper.logException(this.getClass(), e);
        }

    }

    protected static void processPost(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException, IOException {
        doInit(req, resp, ExportType.plain);

        if (user != null && ! user.isRestricted()) {

            final EntityName pointName = CommonFactoryLocator.getInstance().createName(getParam(Parameters.point), EntityType.point);
            final Point point = (Point) EntityServiceFactory.getInstance().getEntityByName(user, pointName,PointEntity.class.getName());

            if (point != null) {


                {

                    final Value v;

                    if (Utils.isEmptyString(getParam(Parameters.json))) {
                        final double latitude = getDoubleFromParam(getParam(Parameters.lat));
                        final double longitude = getDoubleFromParam(getParam(Parameters.lng));
                        final double value = getDoubleFromParam(getParam(Parameters.value));
                        final Date timestamp = (getParam(Parameters.timestamp) != null) ? (new Date(Long.parseLong(getParam(Parameters.timestamp)))) : new Date();
                        v = ValueModelFactory.createValueModel(latitude, longitude, value, timestamp, getParam(Parameters.note), getParam(Parameters.json));
                    } else {
                        final Value vx = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);

                        v = ValueModelFactory.createValueModel(vx.getLatitude(), vx.getLongitude(), vx.getDoubleValue(), vx.getTimestamp(),
                                vx.getNote(), vx.getData(), AlertType.OK);
                    }

                    final Value result = RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
                    final PrintWriter out = resp.getWriter();
                    final String j = GsonFactory.getInstance().toJson(result);
                    out.print(j);

                }
            }
            else {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND));
            }

        }

    }


    public static void processGet(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException, IOException {
        doInit(req, resp, ExportType.plain);
        final PrintWriter out = resp.getWriter();
        Value nv = null;
        final String format = getParam(Parameters.format)==null ? Words.WORD_DOUBLE : getParam(Parameters.format);

        if (format.equals(Parameters.json.getText()) && !Utils.isEmptyString(getParam(Parameters.json))) {
            nv = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);
        } else if (format.equals(Words.WORD_DOUBLE) && !Utils.isEmptyString(getParam(Parameters.value))) {
            nv = ValueModelFactory.createValueModel(
                    getParam(Parameters.value),
                    getParam(Parameters.note),
                    getParam(Parameters.lat),
                    getParam(Parameters.lng),
                    getParam(Parameters.json));
        }
        out.println(processRequest(getParam(Parameters.point), getParam(Parameters.uuid), format, nv, user));
        out.close();


    }

    private static double getDoubleFromParam(final String valueStr) {
        double retVal;
        try {
            retVal = (valueStr != null) ? Double.valueOf(valueStr) : 0;
        } catch (NumberFormatException e) {
            retVal = 0;
        }
        return retVal;
    }

    protected static String processRequest(
            final String pointNameParam,
            final String uuid,
            final String format,
            final Value nv,
            final User u) throws NimbitsException {
        final Point p;

        final String result;

        if (!Utils.isEmptyString(uuid)) {
            p = (Point) EntityServiceFactory.getInstance().getEntityByKey(uuid, PointEntity.class.getName());

        }
        else if (!Utils.isEmptyString(pointNameParam)) {
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            LogHelper.log(ValueServletImpl.class, "Getting point "  + pointNameParam);
            p = (Point) EntityServiceFactory.getInstance().getEntityByName(u, pointName,PointEntity.class.getName());
            if (p == null) {
                throw new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND);

            }


        }
        else {
            throw new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND);
        }


        final Value value;

        if ((u == null || u.isRestricted()) && ! p.getProtectionLevel().equals(ProtectionLevel.everyone)) {
            throw new NimbitsException(UserMessages.RESPONSE_PROTECTED_POINT);
        } else {
            if (nv != null && (u != null && !u.isRestricted())) {
                // record the value, but not if this is a public
                // request
                final Value newValue = ValueModelFactory.createValueModel(
                        nv.getLatitude(), nv.getLongitude(), nv.getDoubleValue(),
                        nv.getTimestamp(), nv.getNote(), nv.getData());


                value = RecordedValueServiceFactory.getInstance().recordValue(u, p, newValue, false);
            } else {
                value = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            }
            if (value!= null) {
            result = format.equals(Parameters.json.getText()) ? GsonFactory.getInstance().toJson(value) : String.valueOf(value.getDoubleValue());
            }
            else {
                result = "";
            }
        }


        return result;
    }


}
