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

import com.google.common.collect.Range;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



@Deprecated
public class PointServletImpl extends ApiServlet  {

    private static final long serialVersionUID = 1L;
    private static final int INT = 1024;
    private static final int EXPIRE = 90;
    private static final double FILTER_VALUE = 0.1;




    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            doInit(req, resp, ExportType.unknown);
            final PrintWriter out = resp.getWriter();


            if (user != null && !user.isRestricted()) {

                String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                        getParam(Parameters.point) : getParam(Parameters.name);
                if (!Utils.isEmptyString(pointNameParam)) {


                    pointNameParam = pointNameParam.trim();

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
                            EntityName parentName = null;
                            EntityType parentType;

                            if (containsParam(Parameters.category)) {
                                parentName = CommonFactory.createName(getParam(Parameters.category), EntityType.category);
                                parentType = EntityType.category;
                            } else if (containsParam(Parameters.parent)) {
                                parentName = CommonFactory.createName(getParam(Parameters.parent), EntityType.point);
                                parentType = EntityType.point;

                            } else {
                                parentType = EntityType.user;
                            }


                            if (!Utils.isEmptyString(pointNameParam) && Utils.isEmptyString(getParam(Parameters.json))) {
                                final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
                                String description = getParam(Parameters.description);
                                final Point point = createPoint(user, pointName, parentName, parentType, description);
                                final String retJson = GsonFactory.getInstance().toJson(point);
                                out.println(retJson);

                            } else if (!Utils.isEmptyString(pointNameParam) && !Utils.isEmptyString(getParam(Parameters.json))) {
                                final Point point = createPointWithJson(user, parentName, parentType, getParam(Parameters.json));
                                final String retJson = GsonFactory.getInstance().toJson(point);
                                out.println(retJson);
                            }
                            break;
                        default:

                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            }
        } catch (IOException e) {
            resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);

        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {

        try {

            final PrintWriter out = resp.getWriter();

            out.print(processGet(req, resp));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void validateExistence(final User user, final EntityName name, final StringBuilder sb) {

        List<Entity> result = entityService.getEntityByName(user, name, EntityType.point);

        if (result.isEmpty()) {
            sb.append("false");
        } else {

            sb.append("true");

        }


    }

    public String processGet(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {

        StringBuilder sb = new StringBuilder(INT);


        doInit(req, resp, ExportType.plain);

        final String actionParam = req.getParameter(Parameters.action.getText());
        final Action action = Utils.isEmptyString(actionParam) ? Action.read : Action.get(actionParam);
        final String startParam = req.getParameter(Parameters.sd.getText());
        final String endParam = req.getParameter(Parameters.ed.getText());
        final String offsetParam = req.getParameter(Parameters.offset.getText());

        //final String format;

        final String pointNameParam = Utils.isEmptyString(getParam(Parameters.name)) ?
                getParam(Parameters.point) : getParam(Parameters.name);
        if (getClientType().equals(ClientType.arduino)) {
            sb.append(Const.CONST_ARDUINO_DATA_SEPARATOR);
        }


        switch (action) {
            case read:
                processGetRead(sb, startParam, endParam, offsetParam, pointNameParam);
                break;
            case validateExists:
                EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
                validateExistence(user, pointName, sb);
                break;
            case list:

                EntityName parentName = CommonFactory.createName(pointNameParam, EntityType.point);
                List<Entity> result = entityService.getEntityByName(user, parentName, EntityType.point);
                if (!result.isEmpty()) {
                    List<Entity> children = entityService.getChildren(user, result);

                    for (Entity e : children) {
                        if (okToReport(user, e)) {

                            sb.append(e.getName().getValue())
                                    .append(",");
                        }
                    }
                    if (sb.toString().endsWith(",")) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                } else {

                }

                break;
        }
        if (getClientType().equals(ClientType.arduino)) {
            sb.append(Const.CONST_ARDUINO_DATA_SEPARATOR);
        }


        resp.setStatus(Const.HTTP_STATUS_OK);
        return sb.toString();
    }

    private void processGetRead(final StringBuilder sb, final String startParam, final String endParam, final String offsetParam, final String pointNameParam) throws Exception {
        if (containsParam(Parameters.uuid)) {

            List<Entity> entityList = entityService.getEntityByKey(user, getParam(Parameters.uuid), EntityType.point);
            if (entityList.isEmpty()) {
                entityList = entityService.getEntityByKey(user, getParam(Parameters.uuid), EntityType.category);
            }
            if (!entityList.isEmpty()) {
                Entity entity = entityList.get(0);
                if (entity.getEntityType().equals(EntityType.point)) {
                    sb.append(outputPoint(getParam(Parameters.count), getParam(Parameters.format), startParam, endParam, offsetParam, entity));
                } else {
                    if (okToReport(user, entity)) {

                        final List<Entity> children = entityService.getChildren(user, entityList);
                        final List<Point> points = new ArrayList<Point>(children.size());


                        for (final Entity e : children) {
                            final Point p = (Point) e;
                            p.setValues(getRecordedValues(getParam(Parameters.count), startParam, endParam, offsetParam, p));
                            List<Value> values = valueService.getCurrentValue(p);
                            if (!values.isEmpty()) {
                                p.setValue(values.get(0));
                            }
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
    }

    private Entity getParentWithParam(final EntityName name, final EntityType parentType, final User u) throws Exception {

        List<Entity> results = entityService.getEntityByName(u, name, parentType);

        if (!results.isEmpty()) {
            return (results.get(0));

        } else {
            List<Entity> user = entityService.getEntityByName(u, CommonFactory.createName(u.getEmail().getValue(), EntityType.user), EntityType.user);
            if (user.isEmpty()) {
                throw new Exception("Error getting parent, this entity has no parent, not even a user!");

            } else {
                return user.get(0);
            }
        }

    }

    public Point createPoint(final User u, final EntityName pointName, final EntityName parentName, final EntityType parentType, String description) throws Exception {
        final String parent;
        if (parentName != null) {
            final Entity category = getParentWithParam(parentName, parentType, u);
            parent = category.getKey();

        } else {
            parent = u.getKey();

        }

        final Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                parent, u.getKey(), UUID.randomUUID().toString());
        Point point = PointModelFactory.createPointModel(entity, 0.0, EXPIRE, "", 0.0,
                false, false, false, 0, false, FilterType.fixedHysteresis, FILTER_VALUE, true, PointType.basic, 0, false, 0.0);


        return (Point) entityService.addUpdateEntity(Arrays.<Entity>asList(point)).get(0);
    }

    private Point createPointWithJson(final User u, final EntityName parentName, final EntityType parentType, final String json) throws Exception {


        final String parent;
        if (parentName != null) {
            final Entity category = getParentWithParam(parentName, parentType, u);
            parent = category != null ? category.getKey() : u.getKey();

        } else {
            parent = u.getKey();
        }


        final Point point = GsonFactory.getInstance().fromJson(json, PointModel.class);
        point.setParent(parent);


        return (Point) entityService.addUpdateEntity(u, Arrays.<Entity>asList(point));

    }

    private Point updatePoint(final User u, final String json) throws Exception {
        final Point point = GsonFactory.getInstance().fromJson(json, PointModel.class);
        return (Point) entityService.addUpdateEntity(u, Arrays.<Entity>asList(point));
        //return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private void deletePoint(final User u, final String pointNameParam) throws Exception {
        final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
        final List<Entity> entity = entityService.getEntityByName(u, pointName, EntityType.point);
        if (!entity.isEmpty()) {
            entityService.deleteEntity(u, entity);

        }
    }

    private String outputPoint(final String countParam, final String format, final String startParam,
                               final String endParam, final String offsetParam, final Entity baseEntity) throws Exception {


        final Point p = (Point) baseEntity;
        p.setValues(getRecordedValues(countParam, startParam, endParam, offsetParam, baseEntity));

        final List<Value> current = valueService.getCurrentValue(p);
        if (!current.isEmpty()) {
            p.setValue(current.get(0));
        }
        final ExportType type = getOutputType(format);
        return type.equals(ExportType.json) ? GsonFactory.getInstance().toJson(p) : "";

    }


    private static ExportType getOutputType(final String format) {

        return Utils.isEmptyString(format) ? ExportType.json : ExportType.valueOf(format);
    }

    private List<Value> getRecordedValues(final String countParam, final String start, final String end, final String offsetParam, final Entity point) throws Exception {

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
            return valueService.getTopDataSeries(point, count);

        } else if (!Utils.isEmptyString(start) && !Utils.isEmptyString(end) && !Utils.isEmptyString(end)) {
            final int offset = Integer.valueOf(offsetParam);
            //  final Timespan ts = TimespanServiceFactory.getInstance().createTimespan(start, end, offset);
            Range ts = Range.closed(start, end);
            return valueService.getDataSegment(point, ts);

        } else {
            return new ArrayList<Value>(0);
        }

    }

    protected String getPointObjects(final String categoryNameParam, final String pointNameParam) {

        if (user != null) {
            Type pointListType = new TypeToken<List<PointModel>>() {
            }.getType();


            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
                final List<Entity> result = entityService.getEntityByName(user, pointName, EntityType.point);
                return result.isEmpty() ? "Error calling " + "Point Service. " + pointNameParam + " not found" : GsonFactory.getInstance().toJson(result.get(0));

            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactory.createName(categoryNameParam, EntityType.category);
                final List<Entity> result = entityService.getEntityByName(user, categoryName, EntityType.category);
                if (result.isEmpty()) {
                    return "Error calling " + "Point Service. " + categoryNameParam + " not found";
                } else {
                    final List<Entity> children = entityService.getChildren(user, result);
                    return GsonFactory.getInstance().toJson(children, pointListType);
                }

            } else {
                return "Missing params";
            }

        } else {
            return UserMessages.RESPONSE_PERMISSION_DENIED;
        }

    }

}
