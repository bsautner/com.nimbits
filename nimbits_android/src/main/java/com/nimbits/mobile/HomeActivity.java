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

package com.nimbits.mobile;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gcm.GCMRegistrar;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.main.async.AddUpdateEntityTask;
import com.nimbits.mobile.main.async.DownloadTreeTask;
import com.nimbits.mobile.main.async.PostValueTask;
import com.nimbits.mobile.main.async.SeriesTask;
import com.nimbits.mobile.startup.async.LoadControlTask;
import com.nimbits.mobile.ui.chart.ChartFragment;
import com.nimbits.mobile.ui.dialog.SimpleEntryDialog;
import com.nimbits.mobile.ui.entitylist.EntityListFragment;
import com.nimbits.mobile.ui.entitylist.EntityListener;
import com.nimbits.mobile.ui.instance.InstanceManager;

import java.util.Date;
import java.util.List;

public class HomeActivity extends Activity implements EntityListener, SeriesTask.SeriesTaskListener  {



    private EntityListFragment entityFragment;
    private ChartFragment chartFragment;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    private static final String TAG = "HomeActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.home_activity_layout);




        if (savedInstanceState == null) {
            Log.v(TAG, "onCreate first time");
            startGcm();
            LoadControlTask loadControlTask = new LoadControlTask();
            loadControlTask.execute();
            showEntityFragment();
            int c = SessionSingleton.getInstance().getDao().getCount(SessionSingleton.getInstance().getServer().getId());
            Log.v(TAG, "Entries in store: " + c );
            if (c == 0) {
                refresh();
            }
            else {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                //entityFragment.show();
            }

        }
        else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        Log.v(TAG, "resume");
        super.onResume();
        startGcm();


//        if (chartFragment != null && SessionSingleton.getInstance().getCurrentEntityPK().getEntityType().equals(EntityType.point)) {
//            showChartFragment();
//        }

    }

    private void showPointFragment() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);
        frame.removeAllViews();
        entityFragment = new EntityListFragment(this);
        entityFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.main_frame, entityFragment).commit();
    }

    private void showEntityFragment() {
        // FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);
        entityFragment = new EntityListFragment(this);
        entityFragment.setArguments(getIntent().getExtras());

        getFragmentManager().beginTransaction().replace(R.id.main_frame, entityFragment, EntityListFragment.TAG).commit();

    }
    private void refresh() {

        final DownloadTreeTask task = new DownloadTreeTask(this);
        task.setListener(new DownloadTreeTask.LoadListener() {


            @Override
            public void onSuccess(int results) {
                Log.v(TAG, "downloaded : " + results);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                // entityFragment.refresh();


            }

            @Override
            public void onProgress(int progress) {

            }
        });
        task.execute();

    }
    private void showChartFragment() {
        Log.v(TAG, "showChartFragment");

        FrameLayout frame = (FrameLayout) findViewById(R.id.data_frame);
        frame.removeAllViews();
        frame.setBackground(getResources().getDrawable(android.R.drawable.toast_frame));
        chartFragment = new ChartFragment(this);
        chartFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.data_frame, chartFragment, ChartFragment.TAG).commit();

        //chartFragment.getSeries(entity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Entity entity = SessionSingleton.getInstance().getCurrentEntity();

        SimpleEntryDialog dialog;
        switch (item.getItemId()) {
            case R.id.action_new_folder:
                dialog = new SimpleEntryDialog(entity.getKey(), EntityType.category, Action.create, "Create new Folder");
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_new_point:
                dialog = new SimpleEntryDialog(entity.getKey(), EntityType.point, Action.create, "Create new data point");
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_expand:
//                if (SessionSingleton.getInstance().getCurrentEntityPK() != null && SessionSingleton.getInstance().getCurrentEntityPK().getEntityType().equals(EntityType.point)) {
//
//
//                    Intent intent = new Intent(getApplicationContext(), ChartViewActivity.class);
//                    Bundle b = new Bundle();
//
//                    intent.putExtras(b);
//                    startActivity(intent);
//                    //  finish();
//                }
                return true;
            case R.id.action_refresh:
                refresh();

                return true;
            case R.id.action_instance:

                Intent intent = new Intent(getApplicationContext(), InstanceManager.class);
                Bundle b = new Bundle();

                intent.putExtras(b);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEntityClicked(Entity entity) {

        if (entity.getEntityType().equals(EntityType.point)) {
            showChartFragment();
        }

        // SessionSingleton.getInstance().setCurrentEntity(entity);

//        List<Entity> children;
//
////        if (checkChildren) {
////            children = SessionSingleton.getInstance().getChildEntities();
////        } else {
//          children = Collections.emptyList();
////        }
//
//        switch (entity.getEntityType()) {
//
//            case user:
//                break;
//            case point:
//                if (children.isEmpty()) {
//                    Log.v(TAG, "onSingleEntitySelected");
//                    showPointFragment();
//                } else {
//                    //showEntity();
//                }
//                showChartFragment();
//                break;
//            case category:
//
//              //  showEntity();
//                break;
//            case subscription:
//                break;
//            case calculation:
//                break;
//            case summary:
//                break;
//            case accessKey:
//                break;
//        }
    }


    @Override
    public void onNewEntity(String parent, EntityType type, EntityName name) {
        Entity entity = EntityModelFactory.createEntity(name, type);
        entity.setOwner(SessionSingleton.getInstance().getEmail());
        entity.setParent(parent );

        AddUpdateEntityTask.getInstance(new AddUpdateEntityTask.AddUpdateEntityTaskListener() {

            @Override
            public void onSuccess(List response) {
                if (response.isEmpty()) {
                    ToastHelper.show(getApplicationContext(), "something went wrong...");
                } else {
                    SessionSingleton.getInstance().getDao().storeTree(SessionSingleton.getInstance().getServer().getId(), response, false);
                    getEntityFragment().refreshData();
                    // entityFragment.refresh();
                    //  SessionSingleton.getInstance().addEntities(response);
                    // showEntityFragment();
                }
            }

            @Override
            public void onFail(Exception ex) {
                ToastHelper.show(getApplicationContext(), ex.getMessage());
            }
        }).execute(entity, EntityModel.class, true);
    }

    @Override
    public void onValueUpdated(String entity, Value response) {

        Log.v(TAG, "Home Activity Updating list on new value");
        if (entityFragment != null) {
            entityFragment.refreshData();
        }


    }

    @Override
    public void onNewValue(final String entity, final String entry) {
        InputMethodManager inputManager = (InputMethodManager)
                getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = this.getCurrentFocus();
        if (focus != null) {
            inputManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        Value value = ValueFactory.createValueFromString(SimpleValue.getInstance(entry), new Date());
        PostValueTask.getInstance(new PostValueTask.PostValueTaskListener() {
            @Override
            public void onSuccess(List<Value> response) throws Exception {
                Log.v(TAG, "Value Recorded");
                if (!response.isEmpty()) {
                    //  SessionSingleton.getInstance().updateCurrentValue(entity, response.get(0));

                }
            }

            @Override
            public void onFail(Exception ex) {
                Log.v(TAG, ex.getMessage());
                ToastHelper.show(getApplication().getApplicationContext(), "Something went wrong:" + ex.getMessage());
            }
        }).execute(entity, value);
    }

    @Override
    public void newValuePrompt(String entity) {
//        SimpleEntryDialog dialog = new SimpleEntryDialog(entity, EntityType.category,
//                Action.recordValue, entity.getName().getValue() +
//                ": Enter Value");
//        dialog.show(getFragmentManager(), "NoticeDialogFragment");

    }

    private EntityListFragment getEntityFragment() {
        if (this.entityFragment == null) {

            this.entityFragment = (EntityListFragment) getFragmentManager().findFragmentByTag(EntityListFragment.TAG);
        }
        return this.entityFragment;
    }


    @Override
    public void onBackPressed() {
        Entity e = SessionSingleton.getInstance().getCurrentEntity();

        if (e.getEntityType().equals(EntityType.user)) {
            finish();
        } else {
            long id = SessionSingleton.getInstance().getDao().getParentId(SessionSingleton.getInstance().getCurrentEntityPK());
            SessionSingleton.getInstance().setCurrentEntity(id);
            getEntityFragment().show();
        }


    }


    private void startGcm() {

        GCMRegistrar.checkDevice(this);

        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //   mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.register(context, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
    //gcm stuff


    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        try {
            GCMRegistrar.onDestroy(this);
        } catch (IllegalArgumentException ex) {

        }

        super.onDestroy();
    }


    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
                    //mDisplay.append(newMessage + "\n");
                }
            };

    @Override
    public void onSuccess(List<Value> response) {

    }

}