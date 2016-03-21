/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api.v3.actions.get;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.v3.actions.RestAction;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class GetAction extends RestAction {

    public static final String APPLICATION_JSON = "application/json";
    public static final double DEFAULT_DISTANCE_METERS = 100.0;


    private final BlobStore blobStore;

    private final GeoSpatialDao geoSpatialDao;

    private final UserDao userDao;

    public GetAction(EntityDao entityDao, EntityService entityService, ValueService valueService, UserService userService,
                     BlobStore blobStore, GeoSpatialDao geoSpatialDao, UserDao userDao) {
        super(entityService, valueService, userService, entityDao);

        this.blobStore = blobStore;
        this.geoSpatialDao = geoSpatialDao;
        this.userDao = userDao;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        resp.setContentType(APPLICATION_JSON);

        String path = req.getRequestURI();

        String base =  getURLPath(req);

        Action action = getAction(path);


        switch (action) {

            case me:
                getMe(req, resp, user, base);
                break;
            case root:
                getUser(user, req, resp,  base);
                break;
            case snapshot:
                getSnapshot(user, req, resp, path, base);
                break;
            case series:
                getSeries(user, req, resp, path);
                break;
            case table:
                getTable(user, req, resp, path);
                break;
            case entity:
                getEntity(user, req, resp, path, base);
                break;
            case nearby:
                getNearbyPoints(req, resp, path, user);
                break;
            case children:
                getChildren(user, resp, path);
                break;
            case file:
                getFile(resp, path);
        }
    }

    private void getFile(HttpServletResponse resp, String path) throws IOException {
        String uuid = getEntityUUID(path);
        Optional<String> file = geoSpatialDao.getFile(uuid);
        if (file.isPresent()) {
            resp.getWriter().print(file.get());
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    //GET Actions
    public void getMe(HttpServletRequest req, HttpServletResponse resp, User user, String base) throws IOException {

        List<Entity> children = getChildEntitiesIfRequested(req, user);
        Integer indx = user.getIsAdmin() ? 0 : null;
        Gson gson = GsonFactory.getInstance(true);
        setHAL(user, user, children, base, indx);
        if (StringUtils.isNotEmpty(req.getParameter(Parameters.point.getText())) ) {
            String name =req.getParameter(Parameters.point.getText());
            Optional<Entity> entityOptional = entityDao.getEntityByName(user, CommonFactory.createName(name, EntityType.point), EntityType.point);
            if (entityOptional.isPresent()) {
                Point point = (Point) entityOptional.get();
                resp.getWriter().println(gson.toJson(point));
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        else if (StringUtils.isNotEmpty(req.getParameter(Parameters.name.getText())) && StringUtils.isNotEmpty(req.getParameter(Parameters.type.getText()))) {
            String name =req.getParameter(Parameters.name.getText());
            int type = Integer.valueOf(req.getParameter(Parameters.type.getText()));
            EntityType entityType = EntityType.get(type);
            Optional<Entity> entityOptional = entityDao.getEntityByName(user, CommonFactory.createName(name, entityType), entityType);
            if (entityOptional.isPresent()) {
                resp.getWriter().println(gson.toJson(entityOptional.get()));
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        else {

            user.setChildren(children);
            String r = gson.toJson(user);
            resp.getWriter().println(r);
        }
    }

    private List<Entity> getChildEntitiesIfRequested(HttpServletRequest req, User user) {
        String childrenParam = req.getParameter(Parameters.children.getText());
        boolean includeChildren = ! StringUtils.isEmpty(childrenParam) && childrenParam.equals("true");
        List<Entity> children;
        if (includeChildren) {
            children = entityDao.getChildren(user, Collections.<Entity>singletonList(user));
        }
        else {
            children = Collections.emptyList();
        }
        return children;
    }


    private void getChildren(User user, HttpServletResponse resp, String path ) throws IOException {
        String uuid = getEntityUUID(path);


        Optional<Entity> optional = entityDao.findEntityByUUID(user, uuid);


        if (optional.isPresent()) {
            List<Entity> children = entityDao.getChildren(user, Collections.singletonList(optional.get()));
            resp.getWriter().println(gson.toJson(children));
        }

    }

    private void getUser(User user, HttpServletRequest req, HttpServletResponse resp,  String base) throws IOException {


        if (user.getIsAdmin()) {
            String email = req.getParameter(Parameters.email.getText());
            String index = req.getParameter("index");
            Integer indx = 0;
            User userResponse;

            if (!StringUtils.isEmpty(email))   {
                userResponse = userDao.getUserByEmail(email);
            }

            else  if (!StringUtils.isEmpty(index))   {
                indx = Integer.parseInt(index);
                userResponse = userDao.getUserByIndex(indx);

            }
            else {
                userResponse = userDao.getUserByIndex(0);
            }
            if (userResponse != null) {
                List<Entity> tree = entityDao.getEntities(userResponse);
                List<Entity> children = new ArrayList<>(tree.size());
                for (Entity e : tree) {
                    if (! e.getEntityType().equals(EntityType.user) && e.getParent().equals(userResponse.getKey())) {
                        children.add(e);
                    }
                }
                setHAL(userResponse, userResponse, children, base, indx);
                String json = gson.toJson(userResponse);
                resp.getWriter().print(json);
            }

        }
        else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only the system admin can do this");
        }
    }

    private void getSeries(User user, HttpServletRequest req, HttpServletResponse resp, String path ) throws IOException {
        String startParam = req.getParameter(Parameters.start.getText());
        String endParam = req.getParameter(Parameters.end.getText());
        String countParam = req.getParameter(Parameters.count.getText());
        String maskParam = req.getParameter(Parameters.mask.getText());

        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.<String>of(maskParam);
        Date start = StringUtils.isEmpty(startParam) ? new Date(1) : new Date(Long.valueOf(startParam));
        Date end = StringUtils.isEmpty(endParam) ? new Date() : new Date(Long.valueOf(endParam));

        Optional<Integer> count = (StringUtils.isEmpty(countParam)) ? Optional.<Integer>absent() : Optional.of(Integer.valueOf(countParam));

        Optional<Range<Integer>> range;
        if (count.isPresent()) {
            range = Optional.of(Range.closed(0, count.get()));
        }
        else {
            range = Optional.absent();
        }

        Optional<Range<Date>> timespan = Optional.of(Range.closed(start, end));


        String uuid = getEntityUUID(path);
        if (user.getUUID().equals(uuid)) {
            List<Entity> entities = entityDao.getEntitiesByType(user, EntityType.point);
            List<Point> response = new ArrayList<>(entities.size());
            for (Entity e: entities) {
                List<Value>values = valueService.getSeries(blobStore, e, timespan, range, mask);

                Point point = (Point) e;
                point.setValues(values);
                response.add(point);

            }
            String json = gson.toJson(response);
            resp.getWriter().print(json);

        }
        else {
            Optional<Entity> optional = entityDao.getEntityByUUID(user, uuid, EntityType.point);
            if (optional.isPresent()) {
                List<Value> values = valueService.getSeries(blobStore, optional.get(), timespan, range, mask);
                String json = gson.toJson(values);
                resp.getWriter().print(json);
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }


    }

    private void getTable(User user, HttpServletRequest req, HttpServletResponse resp, String path ) throws IOException {
        String startParam = req.getParameter(Parameters.start.getText());
        String endParam = req.getParameter(Parameters.end.getText());
        String countParam = req.getParameter(Parameters.count.getText());
        String maskParam = req.getParameter(Parameters.mask.getText());

        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.of(maskParam);
        Optional<Integer> count = StringUtils.isNotEmpty(countParam) ? Optional.of(Integer.valueOf(countParam)) : Optional.<Integer>absent();

        Optional<Range<Date>> timespan;


        if (! StringUtils.isEmpty(startParam) && ! StringUtils.isEmpty(endParam) ) {
            Date start = new Date(Long.valueOf(startParam));
            Date end = new Date(Long.valueOf(endParam));
            timespan = Optional.of(Range.closed(start, end));

        }
        else {
            timespan = Optional.absent();
        }

        if (timespan.isPresent() || count.isPresent()) {
            String uuid = getEntityUUID(path);
            Entity entity = entityDao.getEntityByUUID(user, uuid, EntityType.point).get();
            String chartData = valueService.getChartTable(entityDao, blobStore, user, entity, timespan, count, mask);
            resp.getWriter().print(chartData);

        }
        else {

            throw new RuntimeException(
                    "Please provide a start and end date parameter or a count parameter in unix epoch format including ms for example:?count=100 or ?count=100&mask=regex  or ?start="
                            + (System.currentTimeMillis() - 10000) + "&end=" + System.currentTimeMillis());
        }
    }


    private void getSnapshot(User user, HttpServletRequest req, HttpServletResponse resp, String path, String base) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long sd = calendar.getTimeInMillis();
            long ed = System.currentTimeMillis();

            String[] parts = path.split("/");
            String uuid = parts[parts.length - 2];

            Self self = new Self(String.valueOf(req.getRequestURL()));
            Parent parent = new Parent(base + uuid);
            Sample sample = new Sample(base + uuid + "/series?start=" + sd + "&end=" + ed, "24 hours of data series");

            Links links = new Links(self, parent, sample);


            EmbeddedValues valueEmbedded = null;
            if (StringUtils.isNotEmpty(req.getParameter(Parameters.sd.getText())) && StringUtils.isNotEmpty(req.getParameter(Parameters.ed.getText()))) {

                valueEmbedded = new EmbeddedValues(new ArrayList<Value>());

            }
            Entity entity = entityDao.getEntityByUUID(user, uuid, EntityType.point).get();
            Value snapshot = valueService.getCurrentValue(blobStore, entity);
            ValueContainer valueContainer = new ValueContainer(links, valueEmbedded, snapshot);
            resp.getWriter().println(gson.toJson(valueContainer));
        } catch (Throwable ex)  {
            throw new RuntimeException(ex);
        }
    }





    //Helper Methods

    private void getEntity(User user, HttpServletRequest req, HttpServletResponse resp, String path, String base) throws IOException {
        String uuid = getEntityUUID(path);

        Optional<Entity> optional = entityDao.findEntityByUUID(user, uuid);// entityMap.get(uuid);

        if (optional.isPresent()) {
            Entity entity = optional.get();

            String childrenParam = req.getParameter("children");
            boolean includeChildren = ! StringUtils.isEmpty(childrenParam) && childrenParam.equals("true");
            List<Entity> children;
            if (includeChildren) {
                children = entityDao.getChildren(user, Collections.singletonList(entity));
            }
            else {
                children = Collections.emptyList();
            }

            setHAL(user, entity, children, base, null);

            entity.setChildren(children);

            resp.getWriter().println(gson.toJson(entity));
        }
        else {
            throw new RuntimeException("Entity not found: " + uuid);
        }
    }


    private void getNearbyPoints(HttpServletRequest request, HttpServletResponse resp, String path, User user) throws IOException {

        String metersParam = request.getParameter(Parameters.meters.getText());
        String uuid = getEntityUUID(path);

        Entity entity = entityDao.getEntityByUUID(user, uuid, EntityType.point).get();

        Value current = valueService.getCurrentValue(blobStore, entity);
        double meters = metersParam == null ? DEFAULT_DISTANCE_METERS :  Double.parseDouble(metersParam);

        List<Point> result = geoSpatialDao.getNearby(user, current.getLatitude(), current.getLongitude(), meters);  //entityService.getPointByType(user, PointType.location); //TODO use gps db


        resp.getWriter().print(gson.toJson(result));



    }

    boolean noNulls(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }


    private String getURLPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/service/v3/rest/";
    }
}
