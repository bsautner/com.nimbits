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

package com.nimbits.server.process.task;


import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


public class TaskServiceImpl implements TaskService {

    private static final String IN_CONTENT = "inContent";
    private static final String QUEUE_DELETE_BLOB= "blob";

    private static final String DEFAULT = "default";
    private static final String PATH_DELETE_BLOB_TASK = "/task/deleteBlobTask";
    private static final String PATH_POINT_MAINT_TASK = "/task/pointTask";
    private static final String PATH_MOVE_TASK = "/task/moveTask";
    private static final String PATH_TASK_RECORD_VALUE = "/task/valueTask";
    private static final String PATH_TASK_DUMP_TASK= "/task/dumpTask";
    private static final String PATH_TASK_UPLOAD_TASK= "/task/uploadTask";
    private static final String PATH_TASK_PROCESS_BATCH = "/task/batchTask";
    private static final String PATH_INCOMING_MAIL_QUEUE = "/task/mailTask";
    private static final String PATH_DELETE_DATA_TASK = "/task/deleteTask";
    private static final Logger log = Logger.getLogger(TaskServiceImpl.class.getName());
    private static final String DUMP = "dump";

    private NimbitsEngine engine;

    public TaskServiceImpl(NimbitsEngine engine) {
        this.engine = engine;

    }


    @Override
    public void startDeleteDataTask(Entity point, boolean onlyExpired, int exp) {

    }

    @Override
    public void startDeleteBlobTask(String key) {

    }

    @Override
    public void startDataDumpTask(Entity entity, Timespan timespan) {

    }

    @Override
    public void startUploadTask(User user, Point entity, String blobKey) {

    }

    @Override
    public void startProcessBatchTask(User user, HttpServletRequest req, HttpServletResponse resp) {

    }

    @Override
    public void startRecordValueTask(User u, Entity point, Value value) {

    }

    @Override
    public void startIncomingMailTask(String fromAddress, String inContent) {

    }

    @Override
    public void startPointMaintTask(Entity e) {

    }

    @Override
    public void startMoveCachedValuesToStoreTask(Entity point) {
        ValueService valueService = ValueServiceFactory.getInstance(engine, this);
        valueService.moveValuesFromCacheToStore(point);
    }
}
