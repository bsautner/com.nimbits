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

package com.nimbits.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nimbits.android.database.DatabaseHelperFactory;
import com.nimbits.client.constants.*;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/6/11
 * Time: 4:50 PM
 */
public class LocalDatabaseDaoImpl implements LocalDatabaseDao {
//    @Override
//    public void insertPoints(final Context aContext, final ContentValues values) {
//        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
//        db1.insert(Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY, null, values);
//        db1.close();
//    }

    @Override
    public String getSetting(final Context aContext, final String settingName) {
        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
        final Cursor c = db1.query(Android.ANDROID_TABLE_SETTINGS, new String[]{Android.ANDROID_COL_ID, Android.ANDROID_COL_VALUE}, Android.ANDROID_COL_NAME + "=?", new String[]{settingName}, null, null, null);
        c.moveToFirst();
        final String retVal = c.getString(c.getColumnIndex(Android.ANDROID_COL_VALUE));
        c.close();
        db1.close();
        return retVal;
    }

    public List<String> getServers(final Context aContext) {
        final List<String> serverList = new ArrayList<String>();
        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
        final Cursor c = db1.query(Android.ANDROID_TABLE_SERVERS, new String[]{Android.ANDROID_COL_ID, Android.ANDROID_COL_URL}, null, null, null, null, null);
        c.moveToFirst();
        //l.add("New Connection");

        while (!c.isAfterLast()) {
            final String url = c.getString(c.getColumnIndex(Android.ANDROID_COL_URL));
            serverList.add(url);
            c.moveToNext();
        }
        c.close();
        db1.close();
        return serverList;
    }

//    @Override
//    public void insertMain(final Context aContext, final ContentValues values) {
//        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
//        db1.insert(Const.ANDROID_TABLE_LEVEL_ONE_DISPLAY, null, values);
//        db1.close();
//    }

//    @Override
//    public ListAdapter mainListCursor(final Context aContext) {
//        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
//        final Cursor listCursor = db1.query(Const.ANDROID_TABLE_LEVEL_ONE_DISPLAY, new String[]{Const.ANDROID_COL_ID, Const.ANDROID_COL_NAME, Const.ANDROID_COL_DESCRIPTION, Const.ANDROID_COL_DISPLAY_TYPE}, Const.ANDROID_COL_NAME + " != ?", new String[]{Const.Params.CONST_HIDDEN_CATEGORY}, null, null, Const.ANDROID_COL_DISPLAY_TYPE);
//        //   int r = listCursor.getCount();
//
//
//        return new ImageCursorAdapter(
//                aContext, // Context.
//                R.layout.main_list,  // Specify the row template to use (here, two columns bound to the two retrieved cursor
//                listCursor,                                              // Pass in the cursor to bind to.
//                new String[]{Const.ANDROID_COL_NAME, Const.ANDROID_COL_DESCRIPTION},           // Array of cursor columns to bind to.
//                new int[]{R.id.text1, R.id.text2});
//    }

//    @Override
//    public void updatePointValuesByName(Context aContext, final ContentValues u, final EntityName pointName) {
//        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
//        db1.update(Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY, u, Const.ANDROID_COL_NAME + "=?", new String[]{pointName.getValue()});
//        db1.close();
//
//    }

    public void updateSetting(final Context aContext, final String settingName, final String newValue) {
        final SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
        ContentValues u = new ContentValues();
        u.put(Params.PARAM_VALUE, newValue);
        db1.update(Android.ANDROID_TABLE_SETTINGS, u, Android.ANDROID_COL_NAME + "=?", new String[]{settingName});
        db1.close();
    }

    public void addServer(Context aContext, String url) {

        ContentValues u = new ContentValues();
        u.put(Android.ANDROID_COL_URL, url);
        SQLiteDatabase db1;
        db1 = DatabaseHelperFactory.getInstance(aContext).getDB(true);
        db1.insert(Android.ANDROID_TABLE_SERVERS, null, u);
        db1.close();
    }

    public String getSelectedChildTableJsonByName(Context aContext, String name) {
        Cursor c;
        SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(false);
        c = db1.query(Android.ANDROID_TABLE_LEVEL_TWO_DISPLAY, new String[]{Android.ANDROID_COL_ID, Android.ANDROID_COL_JSON}, Android.ANDROID_COL_NAME + "='" + name + "'", null, null, null, null);
        c.moveToFirst();
        String retVal = c.getString(c.getColumnIndex(Android.ANDROID_COL_JSON));
        c.close();
        db1.close();
        return retVal;


    }

//    @Override
//    public void deleteAll(Context aContext) {
//        SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(aContext).getDB(true);
//        db1.execSQL("delete from " + Const.ANDROID_TABLE_LEVEL_ONE_DISPLAY);
//        db1.execSQL("delete from " + Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY);
//        db1.close();
//    }


}
