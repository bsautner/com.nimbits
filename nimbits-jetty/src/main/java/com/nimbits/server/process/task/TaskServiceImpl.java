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
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.gson.*;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class TaskServiceImpl implements TaskService {
    private final Logger log = Logger.getLogger(TaskServiceImpl.class.getName());
    private static final String IN_CONTENT = "inContent";
    private static final String QUEUE_DELETE_BLOB= "blob";
    private static final String PATH_HB_TASK = "/task/hb";
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
    public static final String UTF_8 = "UTF-8";
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
    public void startRecordValueTask(HttpServletRequest req, User u, Entity point, Value value) {
        if (Double.valueOf(value.getDoubleValue()).isInfinite()) {
            return;
        }

        final String userJson = GsonFactory.getInstance().toJson(u);
        final String pointJson = GsonFactory.getInstance().toJson(point);
        final String valueJson = GsonFactory.getInstance().toJson(value);


        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair(Parameters.pointJson.getText(), pointJson));
        params.add(new BasicNameValuePair(Parameters.pointUser.getText(), userJson));
        params.add(new BasicNameValuePair(Parameters.valueJson.getText(), valueJson));

//        try {
//          //  postTask(req, params, PATH_TASK_RECORD_VALUE);
//        } catch (IOException e) {
//            log.severe(e.getMessage());
//        }

//
//        queue.add(TaskOptions.Builder
//                .withUrl(PATH_TASK_RECORD_VALUE)
//                .param(Parameters.pointUser.getText(), userJson)
//                .param(Parameters.pointJson.getText(), pointJson)
//                .param(Parameters.valueJson.getText(), valueJson));
    }

    @Override
    public void startIncomingMailTask(String fromAddress, String inContent) {

    }

    @Override
    public void startPointMaintTask(HttpServletRequest req, Entity e) {
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
        final String json = gson.toJson(e);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair(Parameters.json.getText(), json));
        try {
            postTask(req, params, PATH_POINT_MAINT_TASK);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public void startMoveCachedValuesToStoreTask(Entity point) {
        ValueService valueService = ValueServiceFactory.getInstance(engine, this);
        valueService.moveValuesFromCacheToStore(point);
    }

    @Override
    public void startHeartbeatTask(HttpServletRequest req, User user, List<Point> entities, Action update)   {
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




        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair(Parameters.json.getText(), json));
            params.add(new BasicNameValuePair(Parameters.user.getText(), userJson));
            params.add(new BasicNameValuePair(Parameters.action.getText(), actionStr));

            postTask(req, params, PATH_HB_TASK);





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void postTask(HttpServletRequest req, List<NameValuePair> params, String path) throws IOException {
        String request = req.getScheme() + "://" +
                req.getServerName() +
                ":" + req.getServerPort() +
                 req.getContextPath() +
                "" + path;

        log.info("Post Task: " + request);
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");


        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(getQuery(params));

        writer.close();
        int r = connection.getResponseCode();
        log.info("Post Task Respond" + r);
    }

    private String getQuery(final List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), UTF_8));
        }

        return result.toString();
    }
}
