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
import com.google.appengine.api.taskqueue.*;
import com.google.gson.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.util.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 2:12 PM
 */
public class TaskImpl implements Task {
    private final Gson gson = GsonFactory.getInstance();
    private static final String TASK_POINT_MAINT = "pointmaint";
    private static final String TASK_MOVE = "move";
    private static final String IN_CONTENT = "inContent";

    private static final String QUEUE_DELETE_SUMMARY = "summary";
    private static final String QUEUE_INCOMING_MAIL = "incommingmail";
    private static final String QUEUE_RECORD_VALUE = "recordvaluequeue";
    private static final String QUEUE_PROCESS_BATCH = "processbatchqueue";
    private static final String QUEUE_DELETE_DATA = "deletedata";
    private static final String DEFAULT = "default";

    private static final String PATH_SUMMARY_TASK = "/task/summary";
    private static final String PATH_POINT_MAINT_TASK = "/task/pointmaint";
    private static final String PATH_UPGRADE_TASK = "/task/upgrade";
    private static final String PATH_MOVE_TASK = "/task/move";
    private static final String PATH_TASK_RECORD_VALUE = "/task/recordvaluetask";
    private static final String PATH_TASK_PROCESS_BATCH = "/task/processbatchtask";
    private static final String PATH_INCOMING_MAIL_QUEUE = "/task/incommingmail";
    private static final String PATH_DELETE_DATA_TASK = "/task/DeleteRecordedValuesTask";
   // private static final Logger log = Logger.getLogger(TaskImpl.class.getName());
    private boolean overrideQueue;


    public TaskImpl() {

        overrideQueue = false;


    }

    @Override
    public void startDeleteDataTask(final Point point,
                                    final boolean onlyExpired,
                                    final int exp) {


        try {
            final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : QUEUE_DELETE_DATA);
            if (onlyExpired) {
                queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_DATA_TASK)
                        .param(Parameters.json.getText(),  GsonFactory.getInstance().toJson(point))
                        .param(Parameters.exp.getText(), Long.toString(exp))

                );
            } else {
                queue.add(TaskOptions.Builder.withUrl(PATH_DELETE_DATA_TASK)
                        .param(Parameters.json.getText(),  GsonFactory.getInstance().toJson(point))
                );
            }
        } catch (Exception e) {
            overrideQueue = true;
            startDeleteDataTask(point, onlyExpired, exp);
        }


    }
    @Override
    public void startSummaryTask(final Entity entity) {
        final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : QUEUE_DELETE_SUMMARY);
        final String json = GsonFactory.getInstance().toJson(entity);
        queue.add(TaskOptions.Builder.withUrl(PATH_SUMMARY_TASK)
                .param(Parameters.json.getText(), json)
        );
    }


    @Override
    public void startProcessBatchTask(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException {


        final com.google.appengine.api.taskqueue.Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : QUEUE_PROCESS_BATCH);
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

        final String userJson = gson.toJson(u);

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
    public void startRecordValueTask(final User u, final Entity point, final Value value, final boolean loopFlag) {
        try {
            if (Double.valueOf(value.getDoubleValue()).isInfinite()) {
                return;
            }
            final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : overrideQueue ? DEFAULT : QUEUE_RECORD_VALUE);
            overrideQueue = false;
            final String userJson = gson.toJson(u);
            final String pointJson = gson.toJson(point);
            final String valueJson = gson.toJson(value);

            queue.add(TaskOptions.Builder
                    .withUrl(PATH_TASK_RECORD_VALUE).taskName(UUID.randomUUID().toString())
                    .param(Parameters.pointUser.getText(), userJson)
                    .param(Parameters.pointJson.getText(), pointJson)
                    .param(Parameters.valueJson.getText(), valueJson)
                    .param(Parameters.loop.getText(), String.valueOf(loopFlag))

            );
        } catch (IllegalStateException ex) {
            overrideQueue = true;
            startRecordValueTask(u, point, value,  loopFlag);
        }

    }

    @Override
    public void startIncomingMailTask(final String fromAddress, final String inContent) {

        final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : QUEUE_INCOMING_MAIL);
        queue.add(TaskOptions.Builder.withUrl(PATH_INCOMING_MAIL_QUEUE)
                .param(Parameters.fromAddress.getText(), fromAddress)
                .param(IN_CONTENT, inContent));


    }

    @Override
    public void startPointMaintTask(final Entity e) {

        try {
        final String json = gson.toJson(e);

        final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : TASK_POINT_MAINT);

        queue.add(TaskOptions.Builder.withUrl(PATH_POINT_MAINT_TASK)
                .param(Parameters.json.getText(), json));
        } catch (IllegalStateException ex) {
            overrideQueue = true;
            startPointMaintTask(e);
        }

    }



//    @Override
//    public void startEntityMaintTask(final User user, ) {
//        final String json = gson.toJson(user);
//
//        final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : Const.TASK_CATEGORY_MAINT);
//
//        queue.add(TaskOptions.Builder.withUrl(Const.PATH_CATEGORY_MAINT_TASK)
//                .param(Const.PARAM_USER, json));
//
//    }

    @Override
    public void startUpgradeTask(final Action action,final  Entity entity, final int s ) {

        try {
        final Queue queue =  QueueFactory.getQueue(DEFAULT);
        String json = "";

        if (entity != null) {
            json = GsonFactory.getInstance().toJson(entity);
        }


        queue.add(TaskOptions.Builder.withUrl(PATH_UPGRADE_TASK)
                .param(Parameters.json.getText(), json)
                 .param("s", String.valueOf(s))
                 .param("e", String.valueOf(s))
                .param(Parameters.action.getText(), action.getCode()));
        }
        catch (IllegalStateException ex) {
            overrideQueue = true;
            startUpgradeTask(action, entity, s  );
        }

    }

    @Override
    public void startMoveCachedValuesToStoreTask(final Entity point) {
        final String json = gson.toJson(point);

        final Queue queue =  QueueFactory.getQueue(overrideQueue ? DEFAULT : TASK_MOVE);

        queue.add(TaskOptions.Builder.withUrl(PATH_MOVE_TASK)
                .param(Parameters.point.getText(), json));
    }


}
