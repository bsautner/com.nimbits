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

package com.nimbits.server.point;

import com.google.gson.Gson;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.common.ServerInfoImpl;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.service.impl.Common;
import com.nimbits.server.timespan.TimespanServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class PointWebService extends HttpServlet {

    private final static Gson gson = GsonFactory.getInstance();

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointWebService.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            final PrintWriter out = resp.getWriter();
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);


            if ((u != null) && (!u.isRestricted())) {

                String pointNameParam = req.getParameter(Const.PARAM_NAME);
                if (pointNameParam == null) {
                    pointNameParam = req.getParameter(Const.PARAM_POINT);
                }
                final String categoryNameParam = req.getParameter(Const.PARAM_CATEGORY);
                final String json = req.getParameter(Const.PARAM_JSON);
                final String actionParam = req.getParameter(Const.PARAM_ACTION);
                final Action action = (Utils.isEmptyString(actionParam)) ? Action.create : Action.get(actionParam);
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam);


                switch (action) {
                    case delete:
                        deletePoint(u, pointNameParam);
                        return;
                    case update:
                        updatePoint(u, json);
                        return;
                    case create:
                        if (!Utils.isEmptyString(pointNameParam) && Utils.isEmptyString(json)) {
                            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
                            final Point point = createPoint(u, pointName, categoryName);
                            final String retJson = gson.toJson(point);
                            out.println(retJson);

                        } else if (!Utils.isEmptyString(json)) {
                            final Point point = createPointWithJson(u, categoryName, json);
                            final String retJson = gson.toJson(point);
                            out.println(retJson);
                        }
                }
            } else {

                out.println(Const.RESPONSE_PERMISSION_DENIED);
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }

    private Entity getCategoryWithParam(final EntityName categoryName, final User u) {

        // Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

        Entity c = EntityServiceFactory.getInstance().getEntityByName(u, categoryName);
        if (c == null) {
            c = EntityServiceFactory.getInstance().getEntityByName(u, u.getName());
        }


        return c;
    }

    private Point createPoint(final User u, final EntityName pointName, final EntityName categoryName) throws NimbitsException {
        Point retObj;
        final Entity category = getCategoryWithParam(categoryName, u);

        Entity entity = EntityModelFactory.createEntity(pointName,"", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                category.getEntity(), u.getUuid() );

        retObj = PointServiceFactory.getInstance().addPoint(u, entity);


        return retObj;
    }

    private Point createPointWithJson(final User u, final EntityName categoryName, final String json) throws NimbitsException {
        Point retObj = null;
        final Entity category = getCategoryWithParam(categoryName, u);

        if (category != null) {
            final Point point = gson.fromJson(json, PointModel.class);
            Entity entity = EntityModelFactory.createEntity(point.getName(),"", EntityType.point,
                    ProtectionLevel.everyone, UUID.randomUUID().toString(),
                    category.getEntity(), u.getUuid() );
            point.setUserFK(u.getId());

            retObj = PointServiceFactory.getInstance().addPoint(u, entity, point);


        }
        return retObj;
    }

    private Point updatePoint(User u, final String json) throws NimbitsException {
        final Point point = gson.fromJson(json, PointModel.class);
        return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private void deletePoint(final User u, final String pointNameParam) throws NimbitsException {
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
        final Entity entity = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
        if (entity != null) {
            EntityServiceFactory.getInstance().deleteEntity(u, entity);
          //  PointServiceFactory.getInstance().deletePoint(u, point);
           // TaskFactoryLocator.getInstance().startDeleteDataTask(point.getId(), false, 0, pointName);
        }
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {

        final String categoryNameParam = req.getParameter(Const.PARAM_CATEGORY);
        String pointNameParam = req.getParameter(Const.PARAM_NAME);
        final String countParam = req.getParameter(Const.PARAM_COUNT);
        final String format = req.getParameter(Const.PARAM_FORMAT);
        final String uuidParam = req.getParameter(Const.PARAM_UUID);
        final String startParam = req.getParameter(Const.PARAM_START_DATE);
        final String endParam = req.getParameter(Const.PARAM_END_DATE);
        final String offsetParam = req.getParameter(Const.PARAM_OFFSET);


        Common.addResponseHeaders(resp, ExportType.plain);

        if (Utils.isEmptyString(pointNameParam)) {
            pointNameParam = req.getParameter(Const.PARAM_POINT);
        }
        User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
        } catch (NimbitsException e) {
            u = null;
        }

        try {
            final PrintWriter out = resp.getWriter();

            final String host = ServerInfoImpl.getFullServerURL(req);
            if (Utils.isEmptyString(uuidParam)) {
                getPointObjects(req, categoryNameParam, pointNameParam, out);
            } else {
                final Point point = PointServiceFactory.getInstance().getPointByUUID(uuidParam);
                if (point != null) {
                    outputPoint(u, host, countParam, format, startParam, endParam, offsetParam, out, point);
                }
                else {
                    final Entity category = EntityServiceFactory.getInstance().getEntityByUUID(u, uuidParam);

                    if (category != null) {
                        if (okToReport(u, category)) {
//                            if (u == null) {
//                                u = UserServiceFactory.getServerInstance().getUserByID(category.getUserFK());
//                                u.setRestricted(true);
//                            }
                            List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(u, category, EntityType.point);
                            final List<Point> points =PointServiceFactory.getInstance().getPoints(u, children);// PointServiceFactory.getInstance().getPointsByCategory(u, category);

                            //todo remove point from list if private
                            for (final Point p : points) {
                                    p.setValues(getRecordedValues(countParam, startParam, endParam, offsetParam, p).getValues());
                                    p.setValue(RecordedValueServiceFactory.getInstance().getCurrentValue(p));
                                    p.setHost(host);


                            }
                            category.setPoints(points);
                            category.setHost(host);
                            final String json = GsonFactory.getInstance().toJson(category);
                            out.print(json);

                        }
                    }
                }
            }

            out.close();
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }

    private void outputPoint(User u, String host, String countParam, String format, String startParam, String endParam, String offsetParam, PrintWriter out, Point point) throws NimbitsException {


            point = getRecordedValues(countParam, startParam, endParam, offsetParam, point);

            Value current = RecordedValueServiceFactory.getInstance().getCurrentValue(point);
            point.setValue(current);
            point.setHost(host);
            final ExportType type = getOutputType(format);

            if (type.equals(ExportType.json)) {
                String json = gson.toJson(point);
                out.print(json);
            }

    }


    private ExportType getOutputType(String format) {
        final ExportType type;
        if (!Utils.isEmptyString(format)) {
            type = ExportType.valueOf(format);

        } else {
            type = ExportType.json;
        }

        return type;
    }

    private Point getRecordedValues(final String countParam, final String start, final String end, String offsetParam, final Point point) throws NimbitsException {
        final Point retPoint;
        if (!Utils.isEmptyString(countParam)) {
            int count;
            try {
                count = Integer.parseInt(countParam);
            } catch (NumberFormatException e) {
                count = 10;
            }
            if (count > 1000) {
                count = 1000;
            }
            retPoint = RecordedValueServiceFactory.getInstance().getTopDataSeries(point, count);

        } else if (!Utils.isEmptyString(start) && !Utils.isEmptyString(end) && !Utils.isEmptyString(end)) {
            int offset = Integer.valueOf(offsetParam);
            Timespan ts = TimespanServiceFactory.getInstance().createTimespan(start, end, offset);

            List<Value> values = RecordedValueServiceFactory.getInstance().getDataSegment(point, ts);
            retPoint = point;
            retPoint.setValues(values);
        } else {
            retPoint = point;
        }
        return retPoint;
    }



    //todo make ok for connections
    private boolean okToReport(User u, Entity c) throws NimbitsException {
        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || !(u == null || u.isRestricted());
    }

    private void getPointObjects(HttpServletRequest req, String categoryNameParam, String pointNameParam, PrintWriter out) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
        if (u != null) {

            final String result;
            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
                final Point p = PointServiceFactory.getInstance().getPointByName(u, pointName);
                result = gson.toJson(p);
                out.println(result);
            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam);
                final Entity c = EntityServiceFactory.getInstance().getEntityByName(u, categoryName);//  CategoryServiceFactory.getInstance().getCategory(u, categoryName);
                List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(u, c, EntityType.point);
                final List<Point> points = PointServiceFactory.getInstance().getPoints(u, children);

                //final List<Point> points = PointServiceFactory.getInstance().getPointsByCategory(u, c);
                result = gson.toJson(points, GsonFactory.pointListType);
                out.println(result);
            }

        } else {
            out.println(Const.RESPONSE_PERMISSION_DENIED);
        }
    }

}