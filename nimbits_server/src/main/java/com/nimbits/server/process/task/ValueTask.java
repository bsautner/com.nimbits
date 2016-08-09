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

package com.nimbits.server.process.task;

import com.google.gson.Gson;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.BaseProcessor;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

@Service
public class ValueTask extends HttpServlet implements BaseProcessor {
    private final Logger logger = LoggerFactory.getLogger(ValueTask.class.getName());

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private SyncService syncService;

    @Autowired
    private ValueService valueService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private DataProcessor dataProcessor;

    @Autowired
    private EntityDao entityDao;


    @Autowired
    private ValueDao valueDao;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        logger.info("value task post");

        String u = req.getParameter(Parameters.user.getText());
        String j = req.getParameter(Parameters.json.getText());
        String id = req.getParameter(Parameters.id.getText());
        Gson gson = GsonFactory.getInstance(true);


        User user = userService.getUserByKey(u).get();
        Value value = gson.fromJson(j, Value.class);


        Point point = (Point) entityDao.getEntity(user, id, EntityType.point).get();


        try {
            process(user, point, value);
        } catch (ValueException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }


    }

    @Override
    public void process(final User user, final Point point, Value value) throws ValueException {

        final boolean ignored = false;
        final boolean ignoredByDate = dataProcessor.ignoreDataByExpirationDate(point, value, ignored);
        final Value sample = valueService.getCurrentValue(point);

        boolean ignoredByCompression = false;

        if (value.getTimestamp() != null && (value.getTimestamp().getTime() > sample.getTimestamp().getTime())) {
            ignoredByCompression = dataProcessor.ignoreByFilter(point, sample, value);
        }


        Value previousValue;

        if (!ignoredByDate && !ignoredByCompression) {

            switch (point.getPointType()) {
                case basic:

                    valueService.recordValues(user, point, Collections.singletonList(value));
                    break;


                case backend:
                    valueService.recordValues(user, point, Collections.singletonList(value));
                    break;
                case cumulative:
                    previousValue = valueService.getCurrentValue(point);


                    if (previousValue.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                        value = new Value.Builder().initValue(value).doubleValue(value.getDoubleValue() + previousValue.getDoubleValue()).create();
                        valueService.recordValues(user, point, Collections.singletonList(value));
                    }
                    break;
                case timespan:
                    valueService.recordValues(user, point, Collections.singletonList(value));
                    break;
                case flag:
                    Integer whole = BigDecimal.valueOf(value.getDoubleValue()).intValue();
                    double d = whole != 0 ? 1.0 : 0.0;
                    value = new Value.Builder().initValue(value).doubleValue(d).create();
                    valueService.recordValues(user, point, Collections.singletonList(value));
                    break;
                case high:
                    previousValue = valueService.getCurrentValue(point);

                    if (value.getDoubleValue() > previousValue.getDoubleValue()) {
                        valueService.recordValues(user, point, Collections.singletonList(value));
                    }

                    break;
                case low:
                    previousValue = valueService.getCurrentValue(point);

                    if (value.getDoubleValue() < previousValue.getDoubleValue()) {
                        valueService.recordValues(user, point, Collections.singletonList(value));
                    }

                    break;
                default:
                    return;

            }


            final AlertType t = valueService.getAlertType(point, value);
            final Value v = new Value.Builder().initValue(value).timestamp(new Date()).alertType(t).create();
            completeRequest(user, point, v);


        } else {
            logger.info("Value was ignored by date or compression setting");
        }


    }


    private void completeRequest(User u,
                                 Point point,
                                 Value value) throws ValueException {
        try {

            if (point.isIdleAlarmOn() && point.getIdleAlarmSent()) {
                point.setIdleAlarmSent(false);
                entityService.addUpdateEntity(valueService, u, point);
            }


            Value snapshot = valueService.getSnapshot(point);
            if (snapshot.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                valueDao.setSnapshot(point, value);
            }

            calculationService.process(u, point, value);

            summaryService.process(u, point, value);

            syncService.process(u, point, value);

            subscriptionService.process(u, point, value);


        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }


    }
}
