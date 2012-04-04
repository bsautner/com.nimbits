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

import com.google.gson.Gson;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class PointServletImpl extends ApiServlet {

    private final static Gson gson = GsonFactory.getInstance();
    private static final long serialVersionUID = 1L;


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            doInit(req, resp, ExportType.unknown);
            final PrintWriter out = resp.getWriter();


            if ((user != null) && (!user.isRestricted())) {

                final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                        getParam(Parameters.point) : getParam(Parameters.name);



                final String actionParam = req.getParameter(Parameters.action.getText());
                final Action action = (Utils.isEmptyString(actionParam)) ? Action.create : Action.get(actionParam);




                switch (action) {
                    case delete:
                        deletePoint(user, pointNameParam);
                        return;
                    case update:
                        updatePoint(user, getParam(Parameters.json));
                        return;
                    case create:
                        EntityName categoryName = null;

                        if (containsParam(Parameters.category)) {
                            categoryName= CommonFactoryLocator.getInstance().createName(getParam(Parameters.category), EntityType.category);
                        }
                        if (!Utils.isEmptyString(pointNameParam) && Utils.isEmptyString(getParam(Parameters.json))) {
                            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                            final Point point = createPoint(user, pointName, categoryName);
                            final String retJson = gson.toJson(point);
                            out.println(retJson);

                        } else if (!Utils.isEmptyString(pointNameParam) && !Utils.isEmptyString(getParam(Parameters.json))) {
                            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                            final Point point = createPointWithJson(user,pointName, categoryName, getParam(Parameters.json));
                            final String retJson = gson.toJson(point);
                            out.println(retJson);
                        }
                }
            } else {

                out.println(UserMessages.RESPONSE_PERMISSION_DENIED);
            }
        } catch (IOException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }
        } catch (NimbitsException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }
        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {


        try {

            final PrintWriter out = resp.getWriter();
            doInit(req, resp, ExportType.plain);


            final String startParam = req.getParameter(Parameters.sd.getText());
            final String endParam = req.getParameter(Parameters.ed.getText());
            final String offsetParam = req.getParameter(Parameters.offset.getText());


            final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                    getParam(Parameters.point) : getParam(Parameters.name);


            if (! containsParam(Parameters.uuid)) {
                getPointObjects(getParam(Parameters.category), pointNameParam, out);
            } else {
                final Point point = PointServiceFactory.getInstance().getPointByKey(getParam(Parameters.uuid));
                if (point != null) {
                    outputPoint(getParam(Parameters.count), getParam(Parameters.format), startParam, endParam, offsetParam, out, point);
                }
                else {
                    final Entity category = EntityServiceFactory.getInstance().getEntityByKey(user, getParam(Parameters.uuid));

                    if (category != null) {
                        if (okToReport(user, category)) {
//                            if (u == null) {
//                                u = UserServiceFactory.getServerInstance().getUserByID(category.getUserFK());
//                                u.setRestricted(true);
//                            }
                            final List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, category, EntityType.point);
                            final List<Point> points =PointServiceFactory.getInstance().getPoints(user, children);// PointServiceFactory.getInstance().getPointsByCategory(u, category);

                            //todo remove point from list if private
                            for (final Point p : points) {
                                p.setValues(getRecordedValues(getParam(Parameters.count), startParam, endParam, offsetParam, p).getValues());
                                p.setValue(RecordedValueServiceFactory.getInstance().getCurrentValue(p));



                            }
                            category.setPoints(points);

                            final String json = GsonFactory.getInstance().toJson(category);
                            out.print(json);

                        }
                    }
                }
            }

            out.close();
        } catch (IOException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }
        } catch (NimbitsException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }
        }

    }
    private static Entity getCategoryWithParam(final EntityName categoryName, final User u) throws NimbitsException {

        // Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

        Entity c = EntityServiceFactory.getInstance().getEntityByName(u, categoryName,EntityType.category);

        if (c == null) {
            c = EntityServiceFactory.getInstance().getEntityByName(u,  CommonFactoryLocator.getInstance().createName(u.getEmail().getValue(), EntityType.user), EntityType.user);
        }


        return c;
    }

    private static Point createPoint(final User u, final EntityName pointName, final EntityName categoryName) throws NimbitsException {
        final Point retObj;
        final Entity category;
        final Entity entity;
        final String parent;
        if (categoryName != null) {
            category = getCategoryWithParam(categoryName, u);
            parent = category.getKey();
        }
        else {
            parent = u.getKey();
        }

        entity = EntityModelFactory.createEntity(pointName,"", EntityType.point, ProtectionLevel.everyone,
                parent, u.getKey());

        retObj = PointServiceFactory.getInstance().addPoint(u, entity);


        return retObj;
    }

    private static Point createPointWithJson(final User u, final EntityName name, final EntityName categoryName, final String json) throws NimbitsException {


        final String parent;
        if (categoryName != null) {
            final Entity category = getCategoryWithParam(categoryName, u);
            if (category != null) {
                parent = category.getKey();
            }
            else {
                parent = u.getKey();
            }

        }
        else {
            parent = u.getKey();
        }


        final Point point = gson.fromJson(json, PointModel.class);

        final Entity entity = EntityModelFactory.createEntity(name,"", EntityType.point,
                ProtectionLevel.everyone,
                parent, u.getKey() );

        return PointServiceFactory.getInstance().addPoint(u, entity, point);

    }

    private static Point updatePoint(final User u, final String json) throws NimbitsException {
        final Point point = gson.fromJson(json, PointModel.class);
        return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private static void deletePoint(final User u, final String pointNameParam) throws NimbitsException {
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
        final Entity entity = EntityServiceFactory.getInstance().getEntityByName(u, pointName,EntityType.point);
        if (entity != null) {
            EntityServiceFactory.getInstance().deleteEntity(u, entity);

        }
    }



    private static void outputPoint(final String countParam, final String format, final String startParam, final String endParam, final String offsetParam, final PrintWriter out, final Point point) throws NimbitsException {


        final Point p = getRecordedValues(countParam, startParam, endParam, offsetParam, point);

        final Value current = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
        p.setValue(current);
        final ExportType type = getOutputType(format);
        if (type.equals(ExportType.json)) {
            final String json = gson.toJson(p);
            out.print(json);
        }

    }


    private static ExportType getOutputType(final String format) {
        final ExportType type;
        if (!Utils.isEmptyString(format)) {
            type = ExportType.valueOf(format);

        } else {
            type = ExportType.json;
        }

        return type;
    }

    private static Point getRecordedValues(final String countParam, final String start, final String end, final String offsetParam, final Point point) throws NimbitsException {
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
            final int offset = Integer.valueOf(offsetParam);
            final Timespan ts = TimespanServiceFactory.getInstance().createTimespan(start, end, offset);

            final List<Value> values = RecordedValueServiceFactory.getInstance().getDataSegment(point, ts);
            retPoint = point;
            retPoint.setValues(values);
        } else {
            retPoint = point;
        }
        return retPoint;
    }



    //todo make ok for connections
    private static boolean okToReport(final User u, final Entity c) {
        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || !(u == null || u.isRestricted());
    }

    private void getPointObjects(final String categoryNameParam, final String pointNameParam, final PrintWriter out) throws NimbitsException {

        if (user != null) {

            final String result;
            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                final Entity e = EntityServiceFactory.getInstance().getEntityByName(user, pointName,EntityType.point);
                if (e != null) {
                    final Point p= PointServiceFactory.getInstance().getPointByKey(e.getKey());
                    result = gson.toJson(p);
                    out.println(result);
                }
                else {
                    FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException("Error calling " +
                            "Point Service. " + pointNameParam + " not found"));
                }
            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam, EntityType.category);
                final Entity c = EntityServiceFactory.getInstance().getEntityByName(user, categoryName,EntityType.category);//  CategoryServiceFactory.getInstance().getCategory(u, categoryName);
                final List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, c, EntityType.point);
                final List<Point> points = PointServiceFactory.getInstance().getPoints(user, children);

                //final List<Point> points = PointServiceFactory.getInstance().getPointsByCategory(u, c);
                result = gson.toJson(points, GsonFactory.pointListType);
                out.println(result);
            }

        } else {
            out.println(UserMessages.RESPONSE_PERMISSION_DENIED);
        }
    }
}

