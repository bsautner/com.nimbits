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

import com.google.gson.JsonSyntaxException;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * GET A current value of a data point.
 * POST a new value to that point
 */
@Deprecated
public class ValueApi extends ApiBase {



    @Autowired
    private TaskService taskService;

    @Autowired
    private ValueService valueService;

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


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        initRequest(req, resp);
        String json = req.getParameter(Parameters.json.getText());

        if (isEmpty(json)) {
            json = getContent(req);

        }


        User user = (User) req.getAttribute(Parameters.user.getText());


        if (user != null && StringUtils.isNotEmpty(json)) {
            String trimmed = json.trim();


            try {
                Entity entity = getEntity(req, resp, user);
                Value value = gson.fromJson(trimmed, Value.class);
                if (value != null) {
                    value.initTimestamp();

                    if (!(entity.getOwner().equals(user.getEmail().getValue()))) {

                        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Owner and poster not equal!");

                    } else {
                        logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));

                        taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask,
                                entityService,
                                blobStore,
                                valueService,
                                summaryService,
                                syncService,
                                subscriptionService,
                                calculationService, dataProcessor, user, (com.nimbits.client.model.point.Point) entity, value);
                        resp.setStatus(HttpServletResponse.SC_OK);

                        completeResponse(resp, "");

                    }

                } else {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Please provide a value object in json format in your querystring " +
                            "e.g json={d:42.1} " + trimmed);
                }
            } catch (NumberFormatException | JsonSyntaxException ex) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "You provided a decimal value in an unsupported format: " + json);
            } catch (ValueException e) {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }


    }


    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {

        initRequest(req, resp);
        User user = (User) req.getAttribute(Parameters.user.getText());


        Entity entitySample = getEntity(req, resp, user);
        {
            Value sample = valueService.getCurrentValue(blobStore, entitySample);

            String json = gson.toJson(sample, Value.class);
            completeResponse(resp, json);


        }
    }


}