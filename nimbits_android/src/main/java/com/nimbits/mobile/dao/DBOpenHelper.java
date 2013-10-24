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
import com.nimbits.mobile.dao.orm.InstanceTable;
import com.nimbits.mobile.dao.orm.TreeTable;

/**
 * Created by benjamin on 10/19/13.
 */
public class DBOpenHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 511;
    private static final String DATABASE_NAME = "NDB" + DATABASE_VERSION;
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null,   DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InstanceTable.getCreateSql());
        db.execSQL(InstanceTable.getInitSQL());
        db.execSQL(TreeTable.getCreateSQL());
     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
