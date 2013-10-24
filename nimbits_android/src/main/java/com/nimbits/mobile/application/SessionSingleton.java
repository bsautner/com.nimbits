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
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.mobile.dao.ApplicationDao;
import com.nimbits.mobile.dao.ApplicationDaoFactory;
import com.nimbits.mobile.dao.DBOpenHelper;


public class SessionSingleton {

    private static SessionSingleton instance;

    private User session;
    private Server server;
    private Context context;
    private String email;
    private long currentEntity;
    private static AndroidControl control;
    private ApplicationInfo appInfo;
    private SQLiteDatabase db;
    private Entity entity;
    private ApplicationDao dao;
    public static void initInstance(Context context)  {
        if (instance == null)
        {

            instance = new SessionSingleton();
            instance.context = context;
            instance.db = new DBOpenHelper(context).getWritableDatabase();
            instance.dao = ApplicationDaoFactory.getInstance();
        }
    }

    public static SessionSingleton getInstance()
    {

        return instance;
    }

    private SessionSingleton() {

    }

    public long getCurrentEntityPK() {
        return currentEntity;
    }

    public void setCurrentEntity(long currentEntity) {
        this.currentEntity = currentEntity;
        this.entity = dao.getEntity(currentEntity);
    }

    public static void setInstance(SessionSingleton instance) {
        SessionSingleton.instance = instance;
    }

    public User getSession() {
        return session;
    }

    public void setSession(User session) {
        this.session = session;

    }

    public Server getServer() {
        if (server ==null) {
            server = ApplicationDaoFactory.getInstance().getServer();
        }
        return server;
    }

    public String getEmail() {

        return email;

    }

     public void setEmail(String email) {
        this.email = email;
    }

    public void setServer() {
        server = ApplicationDaoFactory.getInstance().getServer();

    }




    public AndroidControl getControl() {
        return control == null ? AndroidControlFactory.getConservativeInstance() : control;
    }

    public void setControl(AndroidControl control) {
        SessionSingleton.control = control;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }

    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public ApplicationDao getDao() {
        return dao;
    }

    public Entity getCurrentEntity() {
        return entity == null ? session : entity;
    }
}
