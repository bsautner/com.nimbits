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
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

@Component
public class ValueTask {

    private final Logger logger = LoggerFactory.getLogger(ValueTask.class.getName());

    private EntityService entityService;
    
    private CalculationService calculationService;

    private SummaryService summaryService;
    
    private SyncService syncService;
    
    private ValueService valueService;
    
    private SubscriptionService subscriptionService;
    
    private DataProcessor dataProcessor;



    @Autowired
    public ValueTask(EntityService entityService, CalculationService calculationService, SummaryService summaryService, SyncService syncService,
                     ValueService valueService, SubscriptionService subscriptionService, DataProcessor dataProcessor) {
        this.entityService = entityService;
        this.calculationService = calculationService;
        this.summaryService = summaryService;
        this.syncService = syncService;
        this.valueService = valueService;
        this.subscriptionService = subscriptionService;
        this.dataProcessor = dataProcessor;
    }

    public void process(final User user, final Point point, Value value) throws IOException {

        final boolean ignored = false;
        boolean ignoredByCompression = false;
        final boolean ignoredByDate = dataProcessor.ignoreDataByExpirationDate(point, value, ignored);
        final Value sample = valueService.getCurrentValue(point);



        if (value.getLTimestamp() > sample.getLTimestamp()) {
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


                    if (previousValue.getLTimestamp()  < value.getLTimestamp()) {
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
                                 Value value) throws IOException {


            if (point.isIdleAlarmOn() && point.idleAlarmSent()) {
                point.setIdleAlarmSent(false);
                entityService.addUpdateEntity(u, point);
            }


            calculationService.process(this, u, point, value);

            summaryService.process(this, u, point, value);

            syncService.process(u, point, value);

            subscriptionService.process(u, point, value);




    }
}
