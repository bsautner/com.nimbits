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
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.transactions.service.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.time.*;
import com.nimbits.server.transactions.service.value.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;


public class PointServletImpl extends ApiServlet {

    private final static Gson gson = GsonFactory.getInstance();
    private static final long serialVersionUID = 1L;
    private static final int INT = 1024;
    private static final int EXPIRE = 90;
    private static final double FILTER_VALUE = 0.1;
    final Logger log = Logger.getLogger(PointServletImpl.class.getName());

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            doInit(req, resp, ExportType.unknown);
            final PrintWriter out = resp.getWriter();


            if (user != null &&  ! user.isRestricted()) {

                final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                        getParam(Parameters.point) : getParam(Parameters.name);



                final String actionParam = req.getParameter(Parameters.action.getText());
                final Action action = Utils.isEmptyString(actionParam) ? Action.create : Action.get(actionParam);




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
                            //  final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                            final Point point = createPointWithJson(user, categoryName, getParam(Parameters.json));
                            final String retJson = gson.toJson(point);
                            out.println(retJson);
                        }
                        break;
                    default:

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

            out.print(processGet(req, resp));
        } catch (IOException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }

    protected String processGet(HttpServletRequest req, HttpServletResponse resp) {

        StringBuilder sb = new StringBuilder(INT);
        try {


            doInit(req, resp, ExportType.plain);


            final String startParam = req.getParameter(Parameters.sd.getText());
            final String endParam = req.getParameter(Parameters.ed.getText());
            final String offsetParam = req.getParameter(Parameters.offset.getText());


            final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                    getParam(Parameters.point) : getParam(Parameters.name);


            if (containsParam(Parameters.uuid)) {

                Entity entity =   EntityServiceFactory.getInstance().getEntityByKey(getParam(Parameters.uuid), EntityType.point).get(0);
                if (entity == null) {
                    entity=  EntityServiceFactory.getInstance().getEntityByKey(user, getParam(Parameters.uuid), EntityType.category).get(0);
                }
                if (entity != null) {
                    if (entity.getEntityType().equals(EntityType.point)) {
                        sb.append(outputPoint(getParam(Parameters.count), getParam(Parameters.format), startParam, endParam, offsetParam, entity));
                    }
                    else {
                        if (okToReport(user, entity)) {

                            final List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, entity, EntityType.point);
                            final List<Point> points = new ArrayList<Point>(children.size());


                            for (final Entity e : children) {
                                final Point p = (Point) e;
                                p.setValues(getRecordedValues(getParam(Parameters.count), startParam, endParam, offsetParam, p));
                                p.setValue(ValueServiceFactory.getInstance().getCurrentValue(p));
                                points.add(p);

                            }
                            entity.setChildren(points);

                            final String json = GsonFactory.getInstance().toJson(entity);
                            sb.append(json);

                        }
                    }


                }


            } else {
                sb.append(getPointObjects(getParam(Parameters.category), pointNameParam));
            }


        } catch (NimbitsException e) {
            log.warning(e.getMessage());

        }
        return sb.toString();
    }

    private static Entity getCategoryWithParam(final EntityName categoryName, final User u) throws NimbitsException {

        // Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

        Entity c = EntityServiceFactory.getInstance().getEntityByName(u, categoryName,EntityType.category).get(0);

        if (c == null) {
            c = EntityServiceFactory.getInstance().getEntityByName(u,  CommonFactoryLocator.getInstance().createName(u.getEmail().getValue(), EntityType.user), EntityType.user).get(0);
        }


        return c;
    }

    protected static Point createPoint(final User u, final EntityName pointName, final EntityName categoryName) throws NimbitsException {
        final String parent;
        if (categoryName != null) {
            final Entity category = getCategoryWithParam(categoryName, u);
            parent = category.getKey();
        }
        else {
            parent = u.getKey();
        }

        final Entity entity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,
                parent, u.getKey(), UUID.randomUUID().toString());
        Point point = PointModelFactory.createPointModel(entity,0.0, EXPIRE, "", 0.0,
                false, false, false, 0, false, FilterType.fixedHysteresis, FILTER_VALUE);



        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(point);
    }

    private static Point createPointWithJson(final User u, final EntityName categoryName, final String json) throws NimbitsException {


        final String parent;
        if (categoryName != null) {
            final Entity category = getCategoryWithParam(categoryName, u);
            parent = category != null ? category.getKey() : u.getKey();

        }
        else {
            parent = u.getKey();
        }


        final Point point = gson.fromJson(json, PointModel.class);
        point.setParent(parent);


        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(u, point);

    }

    private static Point updatePoint(final User u, final String json) throws NimbitsException {
        final Point point = gson.fromJson(json, PointModel.class);
        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(u, point);
        //return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private static void deletePoint(final User u, final String pointNameParam) throws NimbitsException {
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
        final List<Entity> entity = EntityServiceFactory.getInstance().getEntityByName(u, pointName,EntityType.point);
        if (! entity.isEmpty()) {
            EntityServiceFactory.getInstance().deleteEntity(u, entity.get(0));

        }
    }



    private static String outputPoint(final String countParam, final String format, final String startParam,
                                      final String endParam, final String offsetParam, final Entity baseEntity) throws NimbitsException {


        final Point p = (Point) baseEntity;
        p.setValues(getRecordedValues(countParam, startParam, endParam, offsetParam, baseEntity));

        final Value current = ValueServiceFactory.getInstance().getCurrentValue(p);
        p.setValue(current);
        final ExportType type = getOutputType(format);
        return type.equals(ExportType.json) ? gson.toJson(p) : "";

    }


    private static ExportType getOutputType(final String format) {

        return Utils.isEmptyString(format) ? ExportType.json : ExportType.valueOf(format);
    }

    private static List<Value>  getRecordedValues(final String countParam, final String start, final String end, final String offsetParam, final Entity point) throws NimbitsException {

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
            return  ValueServiceFactory.getInstance().getTopDataSeries(point, count);

        } else if (!Utils.isEmptyString(start) && !Utils.isEmptyString(end) && !Utils.isEmptyString(end)) {
            final int offset = Integer.valueOf(offsetParam);
            final Timespan ts = TimespanServiceFactory.getInstance().createTimespan(start, end, offset);

            return ValueServiceFactory.getInstance().getDataSegment(point, ts);

        } else {
            return new ArrayList<Value>(0);
        }

    }




    private static boolean okToReport(final User u, final Entity c) {
        return c.getProtectionLevel().equals(ProtectionLevel.everyone) || !(u == null || ! u.isRestricted());
    }

    protected  static String getPointObjects(final String categoryNameParam, final String pointNameParam ) throws NimbitsException {

        if (user != null) {


            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                final List<Entity> result = EntityServiceFactory.getInstance().getEntityByName(user, pointName,EntityType.point);
                return result.isEmpty() ? "Error calling " + "Point Service. " + pointNameParam + " not found" : GsonFactory.getInstance().toJson(result.get(0));

            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam, EntityType.category);
                final List<Entity> result = EntityServiceFactory.getInstance().getEntityByName(user, categoryName,EntityType.category) ;
                if (result.isEmpty()) {
                    return "Error calling " + "Point Service. " + categoryNameParam + " not found";
                } else {
                    final List<Entity> children = EntityServiceFactory.getInstance().getEntityChildren(user, result.get(0), EntityType.point);
                    return gson.toJson(children, GsonFactory.pointListType);
                }

            }
            else {
                return "Missing params";
            }

        } else {
            return UserMessages.RESPONSE_PERMISSION_DENIED;
        }

    }
}

