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

package com.nimbits.server.process.task;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.client.service.calculation.CalculationService;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.intelligence.IntelligenceService;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.client.service.summary.SummaryService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.gson.GsonFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service("valueTask")
@Transactional
public class ValueTask extends HttpServlet implements org.springframework.web.HttpRequestHandler {


    private static final long serialVersionUID = 2L;

    private EntityService entityService;
    private CalculationService calculationService;
    private IntelligenceService  intelligenceService;
    private SummaryService summaryService;
    private SubscriptionService subscriptionService;


    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {



        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final String pointJson = req.getParameter(Parameters.pointJson.getText());
        final String valueJson = req.getParameter(Parameters.valueJson.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
        final Value value = GsonFactory.getInstance().fromJson(valueJson, ValueModel.class);
        final User u = GsonFactory.getInstance().fromJson(userJson, UserModel.class);

        try {

            final Point point = entity instanceof Point
                    ? (Point) entity
                    : (Point) entityService.getEntityByKey(u, entity.getKey(), EntityType.point).get(0);

            if (point.isIdleAlarmOn() && point.getIdleAlarmSent()) {
                point.setIdleAlarmSent(false);
                entityService.addUpdateEntity(u, point);
            }

            //triggers
            calculationService.processCalculations(u, point, value);
            intelligenceService.processIntelligence(u, point);
            summaryService.processSummaries(u, point);
            subscriptionService.processSubscriptions(u, point, value);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());

        }
    }


    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }


    public void setIntelligenceService(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    public IntelligenceService getIntelligenceService() {
        return intelligenceService;
    }

    public void setSummaryService(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    public SummaryService getSummaryService() {
        return summaryService;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }
}