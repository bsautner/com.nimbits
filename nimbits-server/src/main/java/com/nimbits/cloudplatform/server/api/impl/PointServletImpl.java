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

package com.nimbits.cloudplatform.server.api.impl;

import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.constants.UserMessages;
import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.time.TimespanServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service("point")
@Deprecated
public class PointServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {


    private static final long serialVersionUID = 1L;
    private static final int INT = 1024;
    private static final int EXPIRE = 90;
    private static final double FILTER_VALUE = 0.1;


    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {

        if (isPost(req)) {

            doPost(req, resp);
        } else {
            doGet(req, resp);
        }

    }


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
                            log.info("creating point");
                            if (containsParam(Parameters.category)) {
                                parentName = CommonFactory.createName(getParam(Parameters.category), EntityType.category);
                                parentType = EntityType.category;
                            } else if (containsParam(Parameters.parent)) {
                                parentName = CommonFactory.createName(getParam(Parameters.parent), EntityType.point);
                                parentType = EntityType.point;

                            } else {
                                parentType = EntityType.user;
                            }
                            log.info(parentType.name());
                            if (parentName != null) {
                                log.info(parentName.getValue());
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
            LogHelper.logException(this.getClass(), e);
        }
    }


    private void validateExistence(final User user, final EntityName name, final StringBuilder sb) throws Exception {

        List<Entity> result = EntityServiceImpl.getEntityByName(user, name, EntityType.point);

        if (result.isEmpty()) {
            sb.append("false");
        } else {

            sb.append("true");

        }


    }

    public String processGet(final HttpServletRequest req, final HttpServletResponse resp) {

        StringBuilder sb = new StringBuilder(INT);
        try {


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

            log.info(pointNameParam);
            log.info(action.getCode());
            switch (action) {
                case read:
                    processGetRead(sb, startParam, endParam, offsetParam, pointNameParam);
                    break;
                case validateExists:
                    EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
                    validateExistence(user, pointName, sb);
                    break;
                case list:
                    log.info("listing...");
                    EntityName parentName = CommonFactory.createName(pointNameParam, EntityType.point);
                    List<Entity> result = EntityServiceImpl.getEntityByName(user, parentName, EntityType.point);
                    if (!result.isEmpty()) {
                        List<Entity> children = EntityServiceImpl.getChildren(user, result);
                        if (children.isEmpty()) {
                            log.info(parentName.getValue() + " had no children");
                        }
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
                        log.info("couldn't find " + parentName.getValue());
                    }
                    log.info(sb.toString());
                    break;
            }
            if (getClientType().equals(ClientType.arduino)) {
                sb.append(Const.CONST_ARDUINO_DATA_SEPARATOR);
            }

        } catch (Exception e) {
            resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);
            log.warning(e.getMessage());

        }
        resp.setStatus(Const.HTTP_STATUS_OK);
        return sb.toString();
    }

    private void processGetRead(final StringBuilder sb, final String startParam, final String endParam, final String offsetParam, final String pointNameParam) throws Exception {
        if (containsParam(Parameters.uuid)) {

            List<Entity> entityList = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.uuid), EntityType.point);
            if (entityList.isEmpty()) {
                entityList = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.uuid), EntityType.category);
            }
            if (!entityList.isEmpty()) {
                Entity entity = entityList.get(0);
                if (entity.getEntityType().equals(EntityType.point)) {
                    sb.append(outputPoint(getParam(Parameters.count), getParam(Parameters.format), startParam, endParam, offsetParam, entity));
                } else {
                    if (okToReport(user, entity)) {

                        final List<Entity> children = EntityServiceImpl.getChildren(user, entityList);
                        final List<Point> points = new ArrayList<Point>(children.size());


                        for (final Entity e : children) {
                            final Point p = (Point) e;
                            p.setValues(getRecordedValues(getParam(Parameters.count), startParam, endParam, offsetParam, p));
                            List<Value> values = ValueTransaction.getCurrentValue(p);
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

        List<Entity> results = EntityServiceImpl.getEntityByName(u, name, parentType);

        if (!results.isEmpty()) {
            return (results.get(0));

        } else {
            List<Entity> user = EntityServiceImpl.getEntityByName(u, CommonFactory.createName(u.getEmail().getValue(), EntityType.user), EntityType.user);
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
            log.info("parent: " + parent);
        } else {
            parent = u.getKey();
            log.info("parent was null");
        }

        final Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                parent, u.getKey(), UUID.randomUUID().toString());
        Point point = PointModelFactory.createPointModel(entity, 0.0, EXPIRE, "", 0.0,
                false, false, false, 0, false, FilterType.fixedHysteresis, FILTER_VALUE, true, PointType.basic, 0, false, 0.0);


        return (Point) EntityServiceImpl.addUpdateEntity(Arrays.<Entity>asList(point)).get(0);
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


        return (Point) EntityServiceImpl.addUpdateEntity(u, Arrays.<Entity>asList(point));

    }

    private Point updatePoint(final User u, final String json) throws Exception {
        final Point point = GsonFactory.getInstance().fromJson(json, PointModel.class);
        return (Point) EntityServiceImpl.addUpdateEntity(u, Arrays.<Entity>asList(point));
        //return PointServiceFactory.getInstance().updatePoint(u, point);

    }

    private void deletePoint(final User u, final String pointNameParam) throws Exception {
        final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
        final List<Entity> entity = EntityServiceImpl.getEntityByName(u, pointName, EntityType.point);
        if (!entity.isEmpty()) {
            EntityServiceImpl.deleteEntity(u, entity );

        }
    }

    private String outputPoint(final String countParam, final String format, final String startParam,
                               final String endParam, final String offsetParam, final Entity baseEntity) throws Exception {


        final Point p = (Point) baseEntity;
        p.setValues(getRecordedValues(countParam, startParam, endParam, offsetParam, baseEntity));

        final List<Value> current = ValueTransaction.getCurrentValue(p);
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
            return ValueTransaction.getTopDataSeries(point, count);

        } else if (!Utils.isEmptyString(start) && !Utils.isEmptyString(end) && !Utils.isEmptyString(end)) {
            final int offset = Integer.valueOf(offsetParam);
            final Timespan ts = TimespanServiceFactory.getInstance().createTimespan(start, end, offset);

            return ValueTransaction.getDataSegment(point, ts);

        } else {
            return new ArrayList<Value>(0);
        }

    }

    protected String getPointObjects(final String categoryNameParam, final String pointNameParam) throws Exception {

        if (user != null) {
            Type pointListType = new TypeToken<List<PointModel>>() {
            }.getType();


            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactory.createName(pointNameParam, EntityType.point);
                final List<Entity> result = EntityServiceImpl.getEntityByName(user, pointName, EntityType.point);
                return result.isEmpty() ? "Error calling " + "Point Service. " + pointNameParam + " not found" : GsonFactory.getInstance().toJson(result.get(0));

            } else if (!Utils.isEmptyString(categoryNameParam)) {
                final EntityName categoryName = CommonFactory.createName(categoryNameParam, EntityType.category);
                final List<Entity> result = EntityServiceImpl.getEntityByName(user, categoryName, EntityType.category);
                if (result.isEmpty()) {
                    return "Error calling " + "Point Service. " + categoryNameParam + " not found";
                } else {
                    final List<Entity> children = EntityServiceImpl.getChildren(user, result);
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
