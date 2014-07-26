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
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;


public class TaskServiceImpl implements TaskService {

    private static final String IN_CONTENT = "inContent";
    private static final String QUEUE_DELETE_BLOB = "blob";
    private static final String HB_QUEUE = "hb";
    private static final String DEFAULT = "default";
    private static final String PATH_DELETE_BLOB_TASK = "/task/deleteBlobTask";
    private static final String PATH_POINT_MAINT_TASK = "/task/pointTask";
    private static final String PATH_MOVE_TASK = "/task/moveTask";
    private static final String PATH_TASK_RECORD_VALUE = "/task/valueTask";
    private static final String PATH_TASK_DUMP_TASK = "/task/dumpTask";
    private static final String PATH_TASK_UPLOAD_TASK = "/task/uploadTask";
    private static final String PATH_TASK_PROCESS_BATCH = "/task/batchTask";
    private static final String PATH_INCOMING_MAIL_QUEUE = "/task/mailTask";
    private static final String PATH_DELETE_DATA_TASK = "/task/deleteTask";
    private static final String PATH_HB_TASK = "/task/hb";
    private static final Logger log = Logger.getLogger(TaskServiceImpl.class.getName());
    private static final String DUMP = "dump";


    public TaskServiceImpl() {


    }


    @Override
    public void startDeleteDataTask(final Entity point,
                                    final boolean onlyExpired,
                                    final int exp) {


        final Queue queue = QueueFactory.getQueue(DEFAULT);
        if (onlyExpired) {
            queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_DATA_TASK)
                            .param(Parameters.json.getText(), GsonFactory.getInstance().toJson(point))
                            .param(Parameters.exp.getText(), Long.toString(exp))

            );
        } else {
            queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_DATA_TASK)
                            .param(Parameters.json.getText(), GsonFactory.getInstance().toJson(point))
            );
        }


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
        final String userJson = GsonFactory.getInstance().toJson(user);
        queue.add(TaskOptions.Builder.withUrl(PATH_TASK_DUMP_TASK)
                        .param(Parameters.entity.getText(), json)
                        .param(Parameters.user.getText(), userJson)
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
    public void startProcessBatchTask(final User user, final HttpServletRequest req, final HttpServletResponse resp) {


        final Queue queue = QueueFactory.getQueue(DEFAULT);


        final String userJson = GsonFactory.getInstance().toJson(user);

        log.info(userJson);

        final TaskOptions options = TaskOptions.Builder.withUrl(PATH_TASK_PROCESS_BATCH);

        final Enumeration enumeration = req.getParameterNames();
        final Map m = req.getParameterMap();

        while (enumeration.hasMoreElements()) {
            final String param = enumeration.nextElement().toString();
            final String value = ((String[]) m.get(param))[0];
            options.param(param, value);
        }

        options.param(Parameters.pointUser.getText(), userJson);

        queue.add(options);


    }


    @Override
    public void startRecordValueTask(HttpServletRequest req, final User u, final Entity point, final Value value) {

        if (Double.valueOf(value.getDoubleValue()).isInfinite()) {
            return;
        }
        final Queue queue = QueueFactory.getQueue(DEFAULT);
        final String userJson = GsonFactory.getInstance().toJson(u);
        final String pointJson = GsonFactory.getInstance().toJson(point);
        final String valueJson = GsonFactory.getInstance().toJson(value);

        queue.add(TaskOptions.Builder
                .withUrl(PATH_TASK_RECORD_VALUE)
                .param(Parameters.pointUser.getText(), userJson)
                .param(Parameters.pointJson.getText(), pointJson)
                .param(Parameters.valueJson.getText(), valueJson));

    }


    @Override
    public void startIncomingMailTask(final String fromAddress, final String inContent) {

        final Queue queue = QueueFactory.getQueue(DEFAULT);
        queue.add(TaskOptions.Builder.withUrl(PATH_INCOMING_MAIL_QUEUE)
                .param(Parameters.fromAddress.getText(), fromAddress)
                .param(IN_CONTENT, inContent));


    }


    @Override
    public void startPointMaintTask(HttpServletRequest req, final Entity e) {

        final String json = GsonFactory.getInstance().toJson(e);

        final Queue queue = QueueFactory.getQueue(DEFAULT);

        queue.add(TaskOptions.Builder.withUrl(PATH_POINT_MAINT_TASK)
                .param(Parameters.json.getText(), json));

    }


    @Override
    public void startMoveCachedValuesToStoreTask(final Entity point) {
        final String json = GsonFactory.getInstance().toJson(point);

        final Queue queue = QueueFactory.getQueue(DEFAULT);

        queue.add(TaskOptions.Builder.withUrl(PATH_MOVE_TASK)
                .param(Parameters.point.getText(), json));
    }

    @Override
    public void startHeartbeatTask(HttpServletRequest req, User user, List<Point> entities, Action update) {
        Gson gson = new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .registerTypeAdapter(Value.class, new ValueSerializer())
                .registerTypeAdapter(Point.class, new PointSerializer())
                .registerTypeAdapter(Entity.class, new EntitySerializer())
                .registerTypeAdapter(AccessKey.class, new AccessKeySerializer())
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
        final String json =  gson.toJson(entities);
        final String userJson =  gson.toJson(user);
        final String actionStr = update.getCode();

        final Queue queue = QueueFactory.getQueue(HB_QUEUE);

        queue.add(TaskOptions.Builder.withUrl(PATH_HB_TASK)
                        .param(Parameters.json.getText(), json)
                        .param(Parameters.user.getText(), userJson)
                        .param(Parameters.action.getText(), actionStr)
                        .param(Parameters.gae.getText(), "true")
        );

    }


}
