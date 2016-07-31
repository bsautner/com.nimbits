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

package com.nimbits.server.process.cron;


import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.core.task.TaskExecutor;

import java.util.logging.Logger;

public class SystemTaskExecutor {
    private final Logger logger = Logger.getLogger(SystemTaskExecutor.class.getName());


    private final TaskExecutor taskExecutor;

    private final SystemCron systemCron;

    private final EntityDao entityDao;
    private final UserService userService;
    private final EntityService entityService;

    private final ValueService valueService;
    private final TaskService taskService;
    private final SubscriptionService subscriptionService;
    private final CalculationService calculationService;
    private final ValueTask valueTask;
    private final ValueDao valueDao;
    private final SummaryService summaryService;
    private final SyncService syncService;
    private final DataProcessor dataProcessor;
    private final GeoSpatialDao geoSpatialDao;

    public SystemTaskExecutor(GeoSpatialDao geoSpatialDao, TaskExecutor taskExecutor, SystemCron systemCron, EntityDao entityDao,
                              UserService userService, EntityService entityService,
                              ValueService valueService, TaskService taskService, SubscriptionService subscriptionService,
                              CalculationService calculationService, ValueTask valueTask, ValueDao valueDao,
                              SummaryService summaryService, SyncService syncService, DataProcessor dataProcessor) {
        this.taskExecutor = taskExecutor;
        this.systemCron = systemCron;
        this.entityDao = entityDao;
        this.userService = userService;
        this.entityService = entityService;
        this.valueService = valueService;
        this.taskService = taskService;
        this.subscriptionService = subscriptionService;
        this.calculationService = calculationService;
        this.valueTask = valueTask;
        this.valueDao = valueDao;
        this.summaryService = summaryService;
        this.syncService = syncService;
        this.dataProcessor = dataProcessor;
        this.geoSpatialDao = geoSpatialDao;

    }

    private class SystemTask implements Runnable {




        public SystemTask() {

        }

        public void run() {

            try {

                systemCron.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, valueDao, valueService, summaryService, syncService, subscriptionService,
                        calculationService, dataProcessor, null, null, null);


            } catch (Exception e) {
                logger.severe(e.getMessage());
            }

        }

    }



    public void heartbeat() {

        taskExecutor.execute(new SystemTask());

    }

}
