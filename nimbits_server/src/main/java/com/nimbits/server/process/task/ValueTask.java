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

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.core.data.DataProcessor;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

@Component
public class ValueTask {


    private EntityDao entityDao;

    private CalculationService calculationService;

    private SummaryService summaryService;

    private SyncService syncService;

    private ValueService valueService;

    private SubscriptionService subscriptionService;

    private DataProcessor dataProcessor;

    private TaskExecutor taskExecutor;



    @Autowired
    public ValueTask(EntityDao entityDao, CalculationService calculationService, SummaryService summaryService, SyncService syncService,
                     ValueService valueService, SubscriptionService subscriptionService, DataProcessor dataProcessor, TaskExecutor taskExecutor) {

        this.calculationService = calculationService;
        this.summaryService = summaryService;
        this.syncService = syncService;
        this.valueService = valueService;
        this.subscriptionService = subscriptionService;
        this.dataProcessor = dataProcessor;
        this.taskExecutor = taskExecutor;
        this.entityDao = entityDao;

    }

    public void process(final User user, final Point point, Value value) {

        taskExecutor.execute(new ValueRunner(user, point, value, new ValueGeneratedListener() {
            @Override
            public void newValue(User u, Point p, Value v) {
                process(u, p, v);
            }
        }));
    }

    public Value processSync(final User user, final Point point, Value value) {

        ValueRunner valueRunner = new ValueRunner(user, point, value, new ValueGeneratedListener() {
            @Override
            public void newValue(User u, Point p, Value v) {
                processSync(u, p, v);
            }
        });
        return valueRunner.processValue();
    }

    private class ValueRunner implements Runnable {

        private final User user;
        private final Point point;
        private Value value;
        private final ValueGeneratedListener valueGeneratedListener;



        ValueRunner(User user, Point point, Value value, ValueGeneratedListener valueGeneratedListener) {
            this.user = user;
            this.point = point;
            this.value = value;
            this.valueGeneratedListener = valueGeneratedListener;

        }

        @Override
        public void run() {
            processValue();
        }

        Value processValue() {


            boolean ignoredByCompression = false;
            final boolean ignoredByDate = dataProcessor.ignoreDataByExpirationDate(point, value);
            final Value sample = valueService.getCurrentValue(point);


            if (value.getLTimestamp() > sample.getLTimestamp()) {
                ignoredByCompression = dataProcessor.ignoreByFilter(point, sample, value);
            }


            Value previousValue;



            if (ignoredByCompression && ignoredByDate) {
                return value;
            }
            if (ignoredByCompression) {
                return value;
            }
            else if (ignoredByDate) {
                return value;
            }
            else {

                switch (point.getPointType()) {
                    case basic:

                        valueService.recordValues(user, point, Collections.singletonList(value));
                        break;


                    case backend:
                        valueService.recordValues(user, point, Collections.singletonList(value));
                        break;
                    case cumulative:
                        previousValue = valueService.getCurrentValue(point);


                        if (previousValue.getLTimestamp() < value.getLTimestamp()) {
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
                        return value;

                }


                final AlertType t = valueService.getAlertType(point, value);
                final Value v = new Value.Builder().initValue(value).timestamp(System.currentTimeMillis()).alertType(t).create();
                completeRequest(user, point, v);
                return v;


            }


        }


        private void completeRequest(User u,
                                     Point point,
                                     Value value) {


            if (point.isIdleAlarmOn() && point.idleAlarmSent()) {
                entityDao.setIdleAlarmSentFlag(point.getId(), false, false);
            }


            calculationService.process(u, point, value, valueGeneratedListener);

            summaryService.process(u, point, valueGeneratedListener);

            syncService.process(u, point, value);

            subscriptionService.process(u, point, value);


        }
    }
}
