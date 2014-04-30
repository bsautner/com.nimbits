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

package com.nimbits.client.io;


import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.client.android.AndroidControlImpl;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.gson.GsonFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpClientImpl implements NimbitsClient {

    private static final UrlContainer VALUE_SERVICE = UrlContainer.getInstance("/service/v2/value");
    private static final UrlContainer SESSION_SERVICE = UrlContainer.getInstance("/service/v2/session");
    private static final UrlContainer SERIES_SERVICE = UrlContainer.getInstance("/service/v2/series");
    private static final UrlContainer TREE_SERVICE = UrlContainer.getInstance("/service/v2/tree");
    private static final UrlContainer ENTITY_SERVICE = UrlContainer.getInstance("/service/v2/entity");
    private static final UrlContainer HB_SERVICE = UrlContainer.getInstance("/service/v2/hb");
    public static final String HTTP_NIMBITS_GCM_APPSPOT_COM_ANDROID = "http://nimbits-gcm.appspot.com/android";

    public static final int MAX_COUNT = 1000;
    private final HttpHelper helper;
    public final static Type valueListType = new TypeToken<List<ValueModel>>() {
    }.getType();
    public final static Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();
    private final EmailAddress email;
    private final UrlContainer instanceUrl;
    private final String accessKey;

    public HttpClientImpl(Server server, EmailAddress email) {
        this.instanceUrl = UrlContainer.getInstance("http://" + server.getUrl());
        this.email = email;
        this.helper = new HttpHelper(email, server);
        this.accessKey = null;

    }

    public HttpClientImpl(Server server, EmailAddress email, String accessKey) {
        this.instanceUrl = UrlContainer.getInstance("http://" + server.getUrl());
        this.email = email;
        this.helper = new HttpHelper(email, server);
        this.accessKey = accessKey;

    }

    @Override
    public List<User> getSession(List<BasicNameValuePair> params) {

        UrlContainer path = UrlContainer.combine(instanceUrl, SESSION_SERVICE);
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doGet(UserModel.class,
                path,
                params,
                UserModel.class, false);
    }



    @Override
    public List<Value> getValue(final Entity entity) {
        UrlContainer path = UrlContainer.combine(instanceUrl, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doGet(ValueModel.class, path, params, valueListType, false);

    }


    @Override
    public <T> List<T> getTree() {

        UrlContainer path = UrlContainer.combine(instanceUrl, TREE_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doGet(EntityModel.class, path, params,
                entityListType, true);


    }

    @Override
    public List<Value> postValue(final Entity entity, final Value value) {
        UrlContainer path = UrlContainer.combine(instanceUrl, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        String content = GsonFactory.getInstance().toJson(value);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.json.getText(), content)));
        params.add((new BasicNameValuePair(Parameters.email.getText(), email.getValue())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doPost(ValueModel.class, path, params, null, false);


    }

    @Override
    public List<Value> getSeries(final String entity) {
        UrlContainer path = UrlContainer.combine(instanceUrl, SERIES_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity)));
        params.add((new BasicNameValuePair(Parameters.count.getText(), String.valueOf(MAX_COUNT))));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        List<Value> sample = helper.doGet(ValueModel.class, path, params, valueListType, true);
        return sample;

    }
    @Override
    public List<Value> getSeries(final String entity, final Range<Date> range) {
        UrlContainer path = UrlContainer.combine(instanceUrl, SERIES_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(3);

        params.add((new BasicNameValuePair(Parameters.id.getText(), entity)));
        params.add((new BasicNameValuePair(Parameters.sd.getText(), String.valueOf(range.lowerEndpoint().getTime()))));
        params.add((new BasicNameValuePair(Parameters.ed.getText(), String.valueOf(range.upperEndpoint().getTime()))));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        List<Value> sample =  helper.doGet(ValueModel.class, path, params, valueListType, true);
        return sample;

    }
    @Override
    public void deleteEntity(final Entity entity) {

        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), entity.getEntityType().toString())));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.delete.getCode())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        helper.doPost(EntityModel.class, path, params, entityListType, false);

    }

    @Override
    public <T, K> List<T> addEntity(Entity entity, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.create.getCode())));
        params.add((new BasicNameValuePair(Parameters.email.getText(), email.getValue())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doPost(clz, path, params, entityListType, false);

    }


    @Override
    public <T> List<T> updateEntity(Entity entity, Class<T> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.update.getCode())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doPost(clz, path, params, entityListType,true);


    }

    @Override
    public <T, K> List<T> getEntity(final SimpleValue<String> entityId, final EntityType type, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entityId.getValue())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), type.toString())));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        return helper.doGet(clz, path, params, EntityModel.class, false);


    }




    @Override
    public List<AndroidControl> getControl() {
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        String getURL = HTTP_NIMBITS_GCM_APPSPOT_COM_ANDROID;
        HttpGet get = new HttpGet(getURL);
        HttpResponse responseGet;
        List<AndroidControl> result = new ArrayList<AndroidControl>(1);

        try {
            responseGet = client.execute(get);

            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {

                String response = EntityUtils.toString(resEntityGet);
                if (!Utils.isEmptyString(response)) {
                    Gson gson = new GsonBuilder().create();
                    AndroidControl c = gson.fromJson(response, AndroidControlImpl.class);
                    if (c != null) {
                        result.add(c);
                    }
                }


            }
        } catch (IOException e) {
            result.add(AndroidControlFactory.getConservativeInstance());

        } catch (JsonSyntaxException e) {
            result.add(AndroidControlFactory.getConservativeInstance());
        }
        return result;


    }

    @Override
    public void doHeartbeat(Entity parent) {
        UrlContainer path = UrlContainer.combine(instanceUrl, HB_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), parent.getKey())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), String.valueOf(parent.getEntityType().getCode()))));
        if (accessKey != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        }
        helper.doPost(String.class, path, params, entityListType, false);
    }
}
