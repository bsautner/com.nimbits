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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by benjamin on 10/19/13.
 */
public class DBOpenHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 24;
    public static final String INSTANCES_TABLE_NAME = "SERVER_INSTANCES";
    public static final String TREE_TABLE_NAME = "TREE";


    public static final String KEY_URL = "URL";
    public static final String JSON = "JSON";
    public static final String KEY_NAME = "NAME";
    public static final String IS_DEFAULT = "IS_DEFAULT";
    public static final String ID = "_id";
    public static final String FK = "_fk";
    private static final String INSTANCE_TABLE_CREATE =
            "CREATE TABLE " + INSTANCES_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    KEY_URL + " TEXT, " +
                    IS_DEFAULT + " INTEGER, " +
                    KEY_NAME + " TEXT);";


    private static final String TREE_TABLE_CREATE =
            "CREATE TABLE " + TREE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    FK + " INTEGER," +
                    JSON + " TEXT, " +
                    "FOREIGN KEY(" + FK + ") REFERENCES " + INSTANCES_TABLE_NAME + "(" + ID + "))"
                    ;

    private static final String DATABASE_NAME = "NDB" + DATABASE_VERSION;

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

            db.execSQL(INSTANCE_TABLE_CREATE);
            db.execSQL(TREE_TABLE_CREATE);
            db.execSQL("insert into " + INSTANCES_TABLE_NAME + " (" + KEY_URL + ", " + IS_DEFAULT + "," + KEY_NAME + ") " +
                    "VALUES ('cloud.nimbits.com', '1','Public Cloud')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //     db.execSQL("DROP TABLE IF EXISTS " + INSTANCES_TABLE_NAME);
    }
}
