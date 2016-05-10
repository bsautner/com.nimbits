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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;


public class TaskServiceImpl implements TaskService {

    private static final String VALUE = "RecordBackgroundQueue3";

    private static final String PATH_VALUE_TASK = "/task/value";

    public TaskServiceImpl() {
    }



    @Override
    public void process(GeoSpatialDao geoSpatialDao, TaskService taskService, UserService userService, EntityDao entityDao,
                        ValueTask valueTask, EntityService entityService, BlobStore blobStore,
                        ValueService valueService, SummaryService summaryService,
                        SyncService syncService, SubscriptionService subscriptionService,
                        CalculationService calculationService, DataProcessor dataProcessor,
                        User user, Point point, Value value) throws ValueException {
        String u = user.getId();
        String p = point.getId();
        Gson gson =  GsonFactory.getInstance(true);
        String json = gson.toJson(value);

        final Queue queue = QueueFactory.getQueue(VALUE);

        queue.add(TaskOptions.Builder.withUrl(PATH_VALUE_TASK)
                .param(Parameters.user.getText(), u)
                .param(Parameters.json.getText(), json)
                .param(Parameters.id.getText(), p));
    }
}
