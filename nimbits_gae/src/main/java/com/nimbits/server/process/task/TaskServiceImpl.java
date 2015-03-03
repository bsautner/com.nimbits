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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Service
public class TaskServiceImpl implements TaskService {

    private static final String IN_CONTENT = "inContent";
    private static final String QUEUE_DELETE_BLOB = "blob";
    private static final String DELETE_DATA_QUEUE = "deletedata";
    private static final String DEFAULT = "default";
    private static final String VALUE = "value";
    private static final String PATH_DELETE_BLOB_TASK = "/task/deleteBlobTask";

    private static final String PATH_MOVE_TASK = "/task/moveTask";
    private static final String PATH_POINT_TASK = "/task/point";

    private static final String PATH_VALUE_TASK = "/task/value";
    private static final String PATH_TASK_DUMP_TASK = "/task/dumpTask";
    private static final String PATH_TASK_UPLOAD_TASK = "/task/uploadTask";
    private static final String PATH_INCOMING_MAIL_QUEUE = "/task/mailTask";
    private static final String PATH_DELETE_DATA_TASK = "/task/deleteTask";

    private static final String DUMP = "dump";


    public TaskServiceImpl() {


    }


    @Override
    public void startDeleteDataTask(final Point point) {


        final Queue queue = QueueFactory.getQueue(DELETE_DATA_QUEUE);

            queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_DATA_TASK)
                            .param(Parameters.json.getText(), GsonFactory.getInstance().toJson(point)));




    }


    @Override
    public void startDeleteBlobTask(final String key) {

        try {
            final Queue queue = QueueFactory.getQueue(QUEUE_DELETE_BLOB);

            queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_BLOB_TASK)
                            .param(Parameters.key.getText(), key)
            );
        } catch (IllegalStateException e) {
            final Queue queue = QueueFactory.getQueue(DEFAULT);

            queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_BLOB_TASK)
                            .param(Parameters.key.getText(), key)
            );
        }
    }


    @Override
    public void startDataDumpTask(User user, final Entity entity, final Timespan timespan) {
        final Queue queue = QueueFactory.getQueue(DUMP);
        final String json = GsonFactory.getInstance().toJson(entity);
        final String email = user.getEmail().getValue();
        queue.add(TaskOptions.Builder.withUrl(PATH_TASK_DUMP_TASK)
                        .param(Parameters.entity.getText(), json)
                        .param(Parameters.email.getText(), email)
                        .param(Parameters.sd.getText(),
                                String.valueOf(timespan.getStart().getTime()))
                        .param(Parameters.ed.getText(),
                                String.valueOf(timespan.getEnd().getTime()))
        );
    }


    @Override
    public void startUploadTask(final User user, final Point entity, final String blobKey) {
        final Queue queue = QueueFactory.getQueue(DUMP);
        final String json = GsonFactory.getInstance().toJson(entity);
        final String userJson = GsonFactory.getInstance().toJson(user);
        queue.add(TaskOptions.Builder.withUrl(PATH_TASK_UPLOAD_TASK)
                        .param(Parameters.entity.getText(), json)
                        .param(Parameters.user.getText(), userJson)
                        .param(Parameters.blobkey.getText(), blobKey)
        );
    }

    @Override
    public void startIncomingMailTask(final String fromAddress, final String inContent) {

        final Queue queue = QueueFactory.getQueue(DEFAULT);
        queue.add(TaskOptions.Builder.withUrl(PATH_INCOMING_MAIL_QUEUE)
                .param(Parameters.fromAddress.getText(), fromAddress)
                .param(IN_CONTENT, inContent));


    }


    @Override
    public void startPointTask(long pos) {
        final Queue queue = QueueFactory.getQueue("point");

        queue.add(TaskOptions.Builder.withUrl(PATH_POINT_TASK)
                .param(Parameters.cursor.getText(), String.valueOf(pos)));
    }


    @Override
    public void startMoveCachedValuesToStoreTask(final User user, final Entity point) {


        final String id = point.getKey();
        final String userId = user.getKey();


        final Queue queue = QueueFactory.getQueue(DEFAULT);

        queue.add(TaskOptions.Builder.withUrl(PATH_MOVE_TASK)
                .param(Parameters.user.getText(), userId)
                .param(Parameters.id.getText(), id));
    }

    @Override
    public void startRecordValueTask(User user, Point entity, Value value, boolean preAuthorised) {
        String u = user.getKey();
        String p = entity.getKey();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(value);
        String pr = gson.toJson(preAuthorised);
        final Queue queue = QueueFactory.getQueue(VALUE);

        queue.add(TaskOptions.Builder.withUrl(PATH_VALUE_TASK)
                .param(Parameters.user.getText(), u)
                .param(Parameters.json.getText(), json)
                .param(Parameters.isLoggedIn.getText(), pr)
                .param(Parameters.id.getText(), p));
    }


}
