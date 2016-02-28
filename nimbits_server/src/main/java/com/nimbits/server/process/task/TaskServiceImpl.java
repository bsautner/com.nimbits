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


import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskServiceImpl implements TaskService {

    Logger logger = Logger.getLogger(TaskService.class.getName());

    public TaskServiceImpl() {


    }

    @Override
    public void process(
            final GeoSpatialDao geoSpatialDao,
            final TaskService taskService,
            final UserService userService,
            final EntityDao entityDao,
            final ValueTask valueTask,
            final EntityService entityService,
            final BlobStore blobStore,
            final ValueService valueService,
            final SummaryService summaryService,
            final SyncService syncService,
            final SubscriptionService subscriptionService,
            final CalculationService calculationService,
            final DataProcessor dataProcessor,
            final User user, final Point point,
            final Value value) {

        try {
            logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));
            valueTask.process(geoSpatialDao, this, userService, entityDao, valueTask, entityService, blobStore, valueService,
                    summaryService, syncService, subscriptionService, calculationService,
                    dataProcessor, user, point, value);
        } catch (Exception e) {

            logger.log(Level.SEVERE,"Error running value task", e);
        }


    }

}
