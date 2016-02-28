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

package com.nimbits.server.api.v3.actions.post;

import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.Credentials;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.v3.actions.RestAction;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PostAction extends RestAction {


    private final TaskService taskService;
    private final ValueTask valueTask;
    private final BlobStore blobStore;
    private final SummaryService summaryService;
    private final SyncService syncService;
    private final SubscriptionService subscriptionService;
    private final CalculationService calculationService;
    private final DataProcessor dataProcessor;
    private final UserDao userDao;
    private final GeoSpatialDao geoSpatialDao;



    public PostAction(GeoSpatialDao geoSpatialDao, EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, TaskService taskService, ValueTask valueTask, BlobStore blobStore, SummaryService summaryService, SyncService syncService, SubscriptionService subscriptionService, CalculationService calculationService, DataProcessor dataProcessor, UserDao userDao) {
        super(entityService, valueService, userService, entityDao);

        this.taskService = taskService;
        this.valueTask = valueTask;
        this.blobStore = blobStore;
        this.summaryService = summaryService;
        this.syncService = syncService;
        this.subscriptionService = subscriptionService;
        this.calculationService = calculationService;
        this.dataProcessor = dataProcessor;
        this.userDao = userDao;
        this.geoSpatialDao = geoSpatialDao;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        String json = getContent(req);

        String path = req.getRequestURI();

        Action action = getAction(path);

        switch (action) {

            case me:
                break;
            case root:
                postUser(req, resp, json, path);
                break;
            case snapshot:
                try {
                    postSnapshot(user, json, path);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                break;
            case series:
                try {
                    postSeries(user, json, path);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                break;
            case entity:
                postEntity(user, resp, json, path);
                break;
            case file:
                postFile(user, json, path);
        }
    }

    private void postFile(User user, String json, String path) {
        String uuid = getEntityUUID(path);
        geoSpatialDao.addFile(uuid, json);
    }

    //POST Actions


    private void postSnapshot(User user, String json, String path) throws ValueException {



        Value value = gson.fromJson(json, Value.class);
        String uuid = getEntityUUID(path);
        Point entity = (Point) entityDao.getEntityByUUID(user, uuid, EntityType.point);

        taskService.process(geoSpatialDao, taskService, userService, entityDao,
                valueTask, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                calculationService, dataProcessor, user, entity, value);


    }

    private void postSeries(User user, String json, String path) throws ValueException {
        Type listType = new TypeToken<ArrayList<Value>>() {
        }.getType();


        List<Value> values = gson.fromJson(json, listType);
        String uuid = getEntityUUID(path);

        Optional<Entity> optional = entityDao.getEntityByUUID(user, uuid, EntityType.point);
        if (optional.isPresent()) {
            if (values.size() == 1) {

                taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                        calculationService, dataProcessor, user, (Point) optional.get(), values.get(0));

            } else {
                valueService.recordValues(blobStore, user, (Point) optional.get(), values);
            }
        }


    }

    private void postEntity(User user, HttpServletResponse resp, String json, String path) throws IOException {
        String uuid = getEntityUUID(path);

        Optional<Entity> optional = entityDao.findEntityByUUID(user, uuid);

        Map jsonMap = gson.fromJson(json, Map.class);
        int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();


        if (optional.isPresent()) {
            Entity parent = optional.get();
            EntityType type = EntityType.get(t);
            Entity newEntity = (Entity) gson.fromJson(json, type.getClz());
            newEntity.setParent(parent.getKey());
            newEntity.setOwner(user.getKey());
            Entity stored = entityService.addUpdateEntity(valueService, user, newEntity);
            resp.getWriter().print(gson.toJson(stored, stored.getEntityType().getModel()));
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "attempt to add entity with a parent not found - parent uuid: " + uuid);
        }

    }

    private void postUser(HttpServletRequest req, HttpServletResponse resp, String json, String path) throws IOException {
        boolean isFirst = ! userDao.usersExist();



        User user =  GsonFactory.getInstance(false).fromJson(json, UserModel.class);

        User newUser = null;
        if (isFirst) {
            Optional<Credentials> credentials = userService.credentialsWithBasicAuthentication(req);
            if (credentials.isPresent()) {
                newUser = userService.createUserRecord(entityService, valueService, user.getEmail(), credentials.get().getPassword(), UserSource.local);
            }
        }
        else {
            User admin  = userService.getHttpRequestUser(entityService, valueService, req);
            if (admin.getIsAdmin()) {
                newUser = userService.createUserRecord(entityService, valueService, user.getEmail(), user.getPassword(), UserSource.local);


            }
            else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        if (newUser != null) {
            setHAL(newUser, newUser, new ArrayList<Entity>(0), path, null);
            String r = gson.toJson(newUser);
            resp.getWriter().println(r);
        }
        else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User was null");
        }
    }


}
