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

package com.nimbits.server.process.task;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.subscription.SubscriptionServiceFactory;
import com.nimbits.server.transaction.calculation.CalculationServiceFactory;
import com.nimbits.server.transaction.summary.SummaryServiceFactory;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


public class ValueTask extends ApiBase {

    private static final long serialVersionUID = 2L;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)  {
        processRequest(req, resp);
    }
    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)  {
        processRequest(req, resp);
    }
    private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final String pointJson = req.getParameter(Parameters.pointJson.getText());
        final String valueJson = req.getParameter(Parameters.valueJson.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
        final Value value = GsonFactory.getInstance().fromJson(valueJson, ValueModel.class);
        final User u = GsonFactory.getInstance().fromJson(userJson, UserModel.class);

        setup(req, resp);
        final Point point;

        if (entity instanceof Point) {
            point =  (Point) entity;
        }
        else {
            List<Entity> sample =  entityService.getEntityByKey(u, entity.getKey(), EntityType.point);
            if (sample.isEmpty()) {
                return;
            }
            else {
                point = (Point) sample.get(0);
            }
        }

        if (point != null) {
            if (point.isIdleAlarmOn() && point.getIdleAlarmSent()) {
                point.setIdleAlarmSent(false);
                entityService.addUpdateEntity(u, Arrays.<Entity>asList(point));
            }

            CalculationServiceFactory.getInstance(engine, taskService).processCalculations(u, point);
            SummaryServiceFactory.getServiceInstance(engine, taskService).processSummaries(u, point);
            SubscriptionServiceFactory.getServiceInstance(engine, taskService).processSubscriptions(u, point, value);

        }
        //  resp.setStatus(HttpServletResponse.SC_OK);
    }


}