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

package com.nimbits.mobile.application;

import android.content.Context;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.mobile.dao.ApplicationDaoFactory;

/**
 * Created by benjamin on 10/20/13.
 */
public class SessionSingleton {

    private static SessionSingleton instance;

    private User session;
    private Server server;
    private Context context;
    private String email;
    private String apiKey;
    private Entity currentEntity;
    public static void initInstance(Context context)
    {
        if (instance == null)
        {

            instance = new SessionSingleton();
            instance.context = context;

        }
    }

    public static SessionSingleton getInstance()
    {

        return instance;
    }

    private SessionSingleton()
    {

    }

    public static void setInstance(SessionSingleton instance) {
        SessionSingleton.instance = instance;
    }

    public User getSession() {
        return session;
    }

    public void setSession(User session) {
        this.session = session;
        currentEntity = session;
    }

    public Server getServer() {
        if (server ==null) {
            server = ApplicationDaoFactory.getInstance(context).getServer();
        }
        return server;
    }

    public String getEmail() {

        return email;

    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setServer() {
        server = ApplicationDaoFactory.getInstance(context).getServer();

    }

    public Entity getCurrentEntity() {
        return this.currentEntity == null ? this.session : this.currentEntity;
    }

    public void setCurrentEntity(Entity currentEntity) {
        this.currentEntity = currentEntity;
    }
}
