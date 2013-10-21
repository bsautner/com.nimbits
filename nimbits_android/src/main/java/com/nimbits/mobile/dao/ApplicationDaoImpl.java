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

package com.nimbits.mobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.ServerModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.server.gson.GsonFactory;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjamin on 10/20/13.
 */
public class ApplicationDaoImpl implements ApplicationDao {
    final Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();

    private final DBOpenHelper db;
    private final Gson gson = GsonFactory.getInstance();
    public ApplicationDaoImpl(Context context) {

        db = new DBOpenHelper(context);
    }

    @Override
    public Server getServer() {

        Cursor cursor = db.getReadableDatabase().query(DBOpenHelper.INSTANCES_TABLE_NAME, new String[]
                {DBOpenHelper.KEY_URL,DBOpenHelper.IS_DEFAULT, DBOpenHelper.ID},DBOpenHelper.IS_DEFAULT + "=?", new String[]{"1"} ,null, null, null, null );

        cursor.moveToFirst();
        return new ServerModel("http://" + cursor.getString(cursor.getColumnIndex(DBOpenHelper.KEY_URL)),
                cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ID)), SessionSingleton.getInstance().getApiKey());
    }


    @Override
    public void setDefaultInstanceUrl(long id) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.IS_DEFAULT, 0);

        ContentValues update = new ContentValues();
        update.put(DBOpenHelper.IS_DEFAULT, 1);
        db.getWritableDatabase().update(DBOpenHelper.INSTANCES_TABLE_NAME, values, DBOpenHelper.IS_DEFAULT + " = ?", new String[]{"1"});
        db.getWritableDatabase().update(DBOpenHelper.INSTANCES_TABLE_NAME, update, DBOpenHelper.ID + " = ?", new String[]{String.valueOf(id)});
        SessionSingleton.getInstance().setServer();
    }

    @Override
    public List<Entity> getTree(long id) {
        Cursor cursor = db.getReadableDatabase().query(DBOpenHelper.TREE_TABLE_NAME, new String[]
                {DBOpenHelper.JSON},DBOpenHelper.FK + "=?", new String[]{String.valueOf(id)} ,null, null, null, null );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String json = cursor.getString(0);
            return gson.fromJson(json, entityListType);

        }
        else {
            return Collections.emptyList();
        }

    }

    @Override
    public void storeTree(long id, List<Entity> entities) {
          String json = gson.toJson(entities);
        SQLiteDatabase dbs = db.getWritableDatabase();
        if (dbs!= null) {
            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.JSON, json);
            values.put(DBOpenHelper.FK, id);


            dbs.beginTransaction();
            dbs.delete(DBOpenHelper.TREE_TABLE_NAME, DBOpenHelper.FK + " = ?", new String[]{String.valueOf(id)});
            dbs.insert(DBOpenHelper.TREE_TABLE_NAME, null, values);
            dbs.endTransaction();

        }

    }
}
