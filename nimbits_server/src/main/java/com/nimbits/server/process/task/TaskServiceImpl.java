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
import com.nimbits.server.gson.*;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TaskServiceImpl implements TaskService {
    private final Logger log = Logger.getLogger(TaskServiceImpl.class.getName());

    private static final String PATH_HB_TASK = "/task/hb";

    private static final String PATH_POINT_MAINT_TASK = "/task/pointTask";

    public static final String UTF_8 = "UTF-8";


    @Autowired
    private ValueService valueService;


    public TaskServiceImpl() {

    }


    @Override
    public void startDeleteDataTask(Entity point, boolean onlyExpired, int exp) {

    }

    @Override
    public void startDeleteBlobTask(String key) {

    }

    @Override
    public void startDataDumpTask(User user, Entity entity, Timespan timespan) {

    }

    @Override
    public void startUploadTask(User user, Point entity, String blobKey) {

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

        postTask(req, params, PATH_POINT_MAINT_TASK);


    }

    @Override
    public void startMoveCachedValuesToStoreTask(User user, Entity point) throws IOException {

        valueService.moveValuesFromCacheToStore(point);
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
        final String json = gson.toJson(entities);
        final String userJson = gson.toJson(user);
        final String actionStr = update.getCode();


        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair(Parameters.json.getText(), json));
        params.add(new BasicNameValuePair(Parameters.user.getText(), userJson));
        params.add(new BasicNameValuePair(Parameters.action.getText(), actionStr));
        params.add(new BasicNameValuePair(Parameters.gae.getText(), "false"));
        postTask(req, params, PATH_HB_TASK);


    }

    @Override
    public void processNewValueTask(Value v, User user, Point point) {

    }

    protected void postTask(HttpServletRequest req, List<NameValuePair> params, String path) {
        String request = req.getScheme() + "://" +
                req.getServerName() +
                ":" + req.getServerPort() +
                req.getContextPath() +
                "" + path;

        try {
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
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }

    private String getQuery(final List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
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
