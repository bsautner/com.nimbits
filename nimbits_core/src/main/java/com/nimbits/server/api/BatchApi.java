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

package com.nimbits.server.api;


import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
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
import com.nimbits.server.transaction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated post to the series api instead.
 * POST A json array of points populated with values for processing
 */
@Deprecated
public class BatchApi extends ApiBase {

    @Autowired
    TaskService taskService;


    @Autowired
    EntityDao entityDao;

    @Autowired
    CalculationService calculationService;
    @Autowired
    ValueTask valueTask;
    @Autowired
    EntityService entityService;
    @Autowired
    BlobStore blobStore;

    @Autowired
    SummaryService summaryService;
    @Autowired
    SyncService syncService;
    @Autowired
    SubscriptionService subscriptionService;


    @Autowired
    UserService userService;

    @Autowired
    GeoSpatialDao geoSpatialDao;


    public final static Type listType = new TypeToken<Map<String, List<Value>>>() {
    }.getType();


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        initRequest(req, resp);

        User user = (User) req.getAttribute(Parameters.user.getText());


        String json = req.getParameter(Parameters.json.getText());
        if (user != null  && json != null) {


            HashMap<String, List<Value>> map = GsonFactory.getInstance(true).fromJson(json, listType);
            for (String id : map.keySet()) {

                 Point entitySample = (Point) entityDao.getEntity(user, id, EntityType.point).get();

                    List<Value> valueList = map.get(id);

                    for (Value v : valueList) {

                        try {
                            logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));
                            taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                                    calculationService, dataProcessor, user, entitySample, v);
                        } catch (ValueException e) {
                            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        }

                    }


            }


            resp.setStatus(HttpServletResponse.SC_OK);


        }


    }


    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) {


        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);


    }

}