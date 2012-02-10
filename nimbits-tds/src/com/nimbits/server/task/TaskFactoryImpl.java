/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.task;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 2:12 PM
 */
public class TaskFactoryImpl implements TaskFactory {
    private final Gson gson = GsonFactory.getInstance();

    @Override
    public void startDeleteDataTask(final long pointID,
                                    final boolean onlyExpired,
                                    final int exp,
                                    final EntityName pointName) {


        final Queue queue = QueueFactory.getQueue(Const.QUEUE_DELETE_DATA);
        if (onlyExpired) {
            queue.add(TaskOptions.Builder.withUrl(Const.PATH_DELETE_DATA_TASK)
                    .param(Const.PARAM_POINT_ID, Long.toString(pointID))
                    .param(Const.PARAM_EXP, Long.toString(exp))
                    .param(Const.PARAM_NAME, pointName.getValue())

            );
        } else {
            queue.add(TaskOptions.Builder.withUrl(Const.PATH_DELETE_DATA_TASK)
                    .param(Const.PARAM_POINT_ID, Long.toString(pointID))
            );
        }


    }


    @Override
    public void startProcessBatchTask(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException {


        final com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getQueue(Const.QUEUE_PROCESS_BATCH);
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

        final String userJson = gson.toJson(u);

        final TaskOptions options = TaskOptions.Builder.withUrl(Const.PATH_TASK_PROCESS_BATCH);
        final Enumeration enumeration = req.getParameterNames();
        final Map m = req.getParameterMap();

        while (enumeration.hasMoreElements()) {
            final String param = enumeration.nextElement().toString();
            final String value = ((String[]) m.get(param))[0];
            options.param(param, value);
        }

        options.param(Const.PARAM_JSON_USER, userJson);

        queue.add(options);


    }

    @Override
    public void startRecordValueTask(final User u, final Point point, final Value value, final boolean loopFlag) {
        if (Double.valueOf(value.getNumberValue()).isInfinite()) {
            return;
        }
        final Queue queue = QueueFactory.getQueue(Const.QUEUE_RECORD_VALUE);

        final String userJson = gson.toJson(u);
        final String pointJson = gson.toJson(point);
        final String valueJson = gson.toJson(value);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_TASK_RECORD_VALUE)
                .param(Const.PARAM_JSON_USER, userJson)
                .param(Const.PARAM_JSON_POINT, pointJson)
                .param(Const.PARAM_JSON_VALUE, valueJson)
                .param(Const.PARAM_LAT, String.valueOf(value.getLatitude()))
                .param(Const.PARAM_LNG, String.valueOf(value.getLongitude()))
                .param(Const.PARAM_NOTE, value.getNote())
                .param(Const.PARAM_LOOP, String.valueOf(loopFlag))

        );
    }

    @Override
    public void startIncomingMailTask(final String fromAddress, final String inContent) {

        final Queue queue = QueueFactory.getQueue(Const.QUEUE_INCOMING_MAIL);
        queue.add(TaskOptions.Builder.withUrl(Const.PATH_INCOMING_MAIL_QUEUE)
                .param(Const.PARAM_FROM_ADDRESS, fromAddress)
                .param(Const.IN_CONTENT, inContent));


    }

    @Override
    public void startPointMaintTask(final Point point) {
        final String json = gson.toJson(point);

        final Queue queue = QueueFactory.getQueue(Const.TASK_POINT_MAINT);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_POINT_MAINT_TASK)
                .param(Const.PARAM_POINT, json));

    }

    @Override
    public void startCategoryMaintTask(final User user) {
        final String json = gson.toJson(user);

        final Queue queue = QueueFactory.getQueue(Const.TASK_CATEGORY_MAINT);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_CATEGORY_MAINT_TASK)
                .param(Const.PARAM_USER, json));

    }

//    @Override
//    public void startEntityMaintTask(final User user, ) {
//        final String json = gson.toJson(user);
//
//        final Queue queue = QueueFactory.getQueue(Const.TASK_CATEGORY_MAINT);
//
//        queue.add(TaskOptions.Builder.withUrl(Const.PATH_CATEGORY_MAINT_TASK)
//                .param(Const.PARAM_USER, json));
//
//    }

    @Override
    public void startUpgradeTask() {


        final Queue queue = QueueFactory.getQueue(Const.TASK_UPGRADE);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_UPGRADE_TASK));

    }

    @Override
    public void startMoveCachedValuesToStoreTask(Point point) {
        final String json = gson.toJson(point);

        final Queue queue = QueueFactory.getQueue(Const.TASK_MOVE);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_MOVE_TASK)
                .param(Const.PARAM_POINT, json));
    }

    @Override
    public void startUpdatePointStatsTask(final User u, final Point point, final Value v) {

        final com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getQueue(Const.QUEUE_UPDATE_POINT_STATS);
        final String jsonUser = gson.toJson(u);
        final String jsonPoint = gson.toJson(point);
        final String jsonValue = gson.toJson(v);


        queue.add(TaskOptions.Builder.withUrl(Const.PATH_TASK_UPDATE_POINT_STATS)
                .param(Const.PARAM_POINT, jsonPoint)
                .param(Const.PARAM_USER, jsonUser)
                .param(Const.PARAM_VALUE, jsonValue));


    }
}
