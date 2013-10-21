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

package com.nimbits.mobile.ui.instance;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.mobile.HomeActivity;
import com.nimbits.mobile.R;
import com.nimbits.mobile.ToastHelper;
import com.nimbits.mobile.application.NimbitsApplication;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.dao.ApplicationDaoFactory;
import com.nimbits.mobile.dao.DBOpenHelper;
import com.nimbits.mobile.startup.async.StartupTask;
import com.nimbits.mobile.ui.dialog.SimpleEntryDialog;
import com.nimbits.mobile.ui.entitylist.EntityListener;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by benjamin on 10/19/13.
 */
public class InstanceManager extends ListActivity implements EntityListener{
    public static final String TAG = "InstanceManager";
    private ListView listView;
    private ListAdapter adapter;
    Cursor cursor;
    NimbitsApplication app;
    private boolean changedInstance;

    private final DBOpenHelper db = new DBOpenHelper(InstanceManager.this);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance_manager);
        app = (NimbitsApplication) getApplication();


        setCursor();

        listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {

            if (cursor.getLong(cursor.getColumnIndex(DBOpenHelper.IS_DEFAULT)) == 1) {
                  listView.setItemChecked(cursor.getPosition(), true);
            }
            cursor.moveToNext();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
              //  final String url = cursor.getString(cursor.getColumnIndex(DBOpenHelper.KEY_URL));
                ApplicationDaoFactory.getInstance(getApplicationContext()).setDefaultInstanceUrl(id);
                StartupTask.getInstance(new StartupTask.StartupListener() {
                    @Override
                    public void onLoginSuccess(List<User> response) {
                        SessionSingleton.getInstance().setSession((response.get(0)));

                        ToastHelper.show(InstanceManager.this, "Connected!");


                    }

                    @Override
                    public void onLoginFail() {
                        ToastHelper.show(InstanceManager.this, "Could not connect, is the instance running?");

                    }
                }).execute(getApplication());
            }
        });


    }



    private void setCursor() {
        cursor = db.getReadableDatabase().query(DBOpenHelper.INSTANCES_TABLE_NAME, new String[] {DBOpenHelper.ID, DBOpenHelper.KEY_URL,DBOpenHelper.IS_DEFAULT},null, null ,null, null, null, null );


        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_single_choice,
                cursor,

                new String[] {DBOpenHelper.KEY_URL},

                new int[] {android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.instance_manager_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SimpleEntryDialog dialog;
        long ids[] = listView.getCheckedItemIds();
        final long id;
        if (ids.length > 0) {
            id = ids[0];
        }
        else {
            id = -1;
        }
        switch (item.getItemId()) {

            case R.id.action_instance:
                dialog = new SimpleEntryDialog(SessionSingleton.getInstance().getCurrentEntity(), EntityType.server, Action.create, "Connect To a new Nimbits Instance");
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_delete:

                db.getWritableDatabase().execSQL("delete from " + DBOpenHelper.INSTANCES_TABLE_NAME + " where (" + DBOpenHelper.ID + " = " + id  + ")");
                setCursor();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEntityClicked(Entity entity, boolean checkChildren) {

    }

    @Override
    public void onNewEntity(Entity parent, EntityType type, EntityName name) {
        Log.v(TAG, "new instance");
        String n = name.getValue();
        n = n.replace("http://", "").replace("https://", "");
        if (! StringUtils.isEmpty(n)) {

            db.getWritableDatabase().execSQL("insert into " + DBOpenHelper.INSTANCES_TABLE_NAME + " (" + DBOpenHelper.KEY_URL + ", " + DBOpenHelper.IS_DEFAULT + "," + DBOpenHelper.KEY_NAME + ") " +
                    "VALUES ('" + n + "', '1','Not Set')");
            setCursor();
        }
    }

    @Override
    public void onValueUpdated(Entity entity, Value response) {

    }

    @Override
    public void onNewValue(Entity entity, String entry) {

    }

    @Override
    public void newValuePrompt(Entity entity) {

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        Bundle b = new Bundle();

        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}