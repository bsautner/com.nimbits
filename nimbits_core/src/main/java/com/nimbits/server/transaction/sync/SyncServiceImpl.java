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

package com.nimbits.server.transaction.sync;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SyncServiceImpl implements SyncService {
    protected final static Logger log = LoggerFactory.getLogger(SyncServiceImpl.class.getName());




    public SyncServiceImpl( ) {


    }

    @Override
    public void process(final GeoSpatialDao geoSpatialDao,
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
                        final User user, final Point point, final Value value) {
        final Optional<Entity> optional = entityDao.getEntityByTrigger(user, point, EntityType.sync);
        if (optional.isPresent()) {

            Sync sync = (Sync) optional.get();
            String u = sync.getTargetInstance();

            Nimbits nimbits = new Nimbits.Builder()
                    .instance(u)
                    .email(user.getEmail().getValue())
                    .token(sync.getAccessKey()).create();

            // Point target = (Point) entityDao.getEntity(user,((Sync) syncEntity).getTargetPoint(), EntityType.point );
            String targetName = sync.getTarget().split("/")[1];

            nimbits.recordValue(targetName, value);

        }
    }


}
