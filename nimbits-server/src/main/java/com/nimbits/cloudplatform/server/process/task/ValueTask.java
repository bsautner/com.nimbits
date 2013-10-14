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

package com.nimbits.cloudplatform.server.process.task;


import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.calculation.CalculationTransaction;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.subscription.SubscriptionService;
import com.nimbits.cloudplatform.server.transactions.summary.SummaryService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Service("valueTask")

public class ValueTask extends HttpServlet implements org.springframework.web.HttpRequestHandler {


    private static final long serialVersionUID = 2L;

    private final EntityService entityService = EntityServiceFactory.getInstance();

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {



        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final String pointJson = req.getParameter(Parameters.pointJson.getText());
        final String valueJson = req.getParameter(Parameters.valueJson.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
        final Value value = GsonFactory.getInstance().fromJson(valueJson, ValueModel.class);
        final User u = GsonFactory.getInstance().fromJson(userJson, UserModel.class);


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

            CalculationTransaction.processCalculations(u, point);
            SummaryService.processSummaries(u, point);
            SubscriptionService.processSubscriptions(u, point, value);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }


}