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

import com.google.gson.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.api.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;
import com.nimbits.server.time.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;


public class PointServletImpl extends ApiServlet {

    private final static Gson gson = GsonFactory.getInstance();
    private static final long serialVersionUID = 1L;


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            init(req, resp, ExportType.unknown);
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
            init(req, resp, ExportType.plain);


            final String startParam = req.getParameter(Parameters.sd.getText());
            final String endParam = req.getParameter(Parameters.ed.getText());
            final String offsetParam = req.getParameter(Parameters.offset.getText());


            final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                    getParam(Parameters.point) : getParam(Parameters.name);


            if (! containsParam(Parameters.uuid)) {
                getPointObjects(req, getParam(Parameters.category), pointNameParam, out);
            } else {
                final Point point = PointServiceFactory.getInstance().getPointByUUID(getParam(Parameters.uuid));
                if (point != null) {
                    outputPoint(getParam(Parameters.count), getParam(Parameters.format), startParam, endParam, offsetParam, out, point);
                }
                else {
                    final Entity category = EntityServiceFactory.getInstance().getEntityByUUID(user, getParam(Parameters.uuid));

                    if (category != null) {
                        if (okToReport(user, category)) {
//                            if (u == null) {
//                                u = UserServiceFactory.getServerInstance().getUserByID(category.getUserFK());
//                                u.setRestricted(true);
//                            }
                            List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, category, EntityType.point);
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
        final Entity category;
        Entity entity;
        String parent;
        if (categoryName != null) {
            category = getCategoryWithParam(categoryName, u);
            parent = category.getEntity();
        }
        else {
            parent = u.getUuid();
        }

        entity = EntityModelFactory.createEntity(pointName,"", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                parent, u.getUuid() );

        retObj = PointServiceFactory.getInstance().addPoint(u, entity);


        return retObj;
    }

    private Point createPointWithJson(final User u, final EntityName name, final EntityName categoryName, final String json) throws NimbitsException {


        final String parent;
        if (categoryName != null) {
            final Entity category = getCategoryWithParam(categoryName, u);
            if (category != null) {
                parent = category.getEntity();
            }
            else {
                parent = u.getUuid();
            }

        }
        else {
            parent = u.getUuid();
        }


        final Point point = gson.fromJson(json, PointModel.class);

        Entity entity = EntityModelFactory.createEntity(name,"", EntityType.point,
                ProtectionLevel.everyone, UUID.randomUUID().toString(),
                parent, u.getUuid() );
        point.setUserFK(u.getId());

        return PointServiceFactory.getInstance().addPoint(u, entity, point);

    }

    private Point updatePoint(User u, final String json) throws NimbitsException {
        final Point point = gson.fromJson(json, PointModel.class);
        return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private void deletePoint(final User u, final String pointNameParam) throws NimbitsException {
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
        final Entity entity = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
        if (entity != null) {
            EntityServiceFactory.getInstance().deleteEntity(u, entity);

        }
    }



    private void outputPoint(String countParam, String format, String startParam, String endParam, String offsetParam, PrintWriter out, Point point) throws NimbitsException {


        point = getRecordedValues(countParam, startParam, endParam, offsetParam, point);

        Value current = RecordedValueServiceFactory.getInstance().getCurrentValue(point);
        point.setValue(current);
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
    private boolean okToReport(User u, Entity c) {
        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || !(u == null || u.isRestricted());
    }

    private void getPointObjects(HttpServletRequest req, String categoryNameParam, String pointNameParam, PrintWriter out) throws NimbitsException {

        if (user != null) {

            final String result;
            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                final Entity e = EntityServiceFactory.getInstance().getEntityByName(user, pointName);
                if (e != null) {
                    final Point p= PointServiceFactory.getInstance().getPointByUUID(e.getEntity());
                    result = gson.toJson(p);
                    out.println(result);
                }
                else {
                    FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException("Error calling " +
                            "Point Service. " + pointNameParam + " not found"));
                }
            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam, EntityType.category);
                final Entity c = EntityServiceFactory.getInstance().getEntityByName(user, categoryName);//  CategoryServiceFactory.getInstance().getCategory(u, categoryName);
                List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, c, EntityType.point);
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

