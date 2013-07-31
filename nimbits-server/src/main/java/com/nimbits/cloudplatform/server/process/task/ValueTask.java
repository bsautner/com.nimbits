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
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.subscription.SubscriptionService;
import com.nimbits.cloudplatform.server.transactions.summary.SummaryService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


@Service("valueTask")

public class ValueTask extends HttpServlet implements org.springframework.web.HttpRequestHandler {


    private static final long serialVersionUID = 2L;



    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {



        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final String pointJson = req.getParameter(Parameters.pointJson.getText());
        final String valueJson = req.getParameter(Parameters.valueJson.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
        final Value value = GsonFactory.getInstance().fromJson(valueJson, ValueModel.class);
        final User u = GsonFactory.getInstance().fromJson(userJson, UserModel.class);


        final Point point = entity instanceof Point
                ? (Point) entity
                : (Point) EntityServiceImpl.getEntityByKey(u, entity.getKey(), EntityType.point).get(0);

        if (point.isIdleAlarmOn() && point.getIdleAlarmSent()) {
            point.setIdleAlarmSent(false);
            EntityServiceImpl.addUpdateEntity(u, Arrays.<Entity>asList(point));
        }

        CalculationTransaction.processCalculations(u, point);
        SummaryService.processSummaries(u, point);
        SubscriptionService.processSubscriptions(u, point, value);
        resp.setStatus(HttpServletResponse.SC_OK);

}


}