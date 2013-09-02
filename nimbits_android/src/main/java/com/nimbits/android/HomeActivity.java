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

package com.nimbits.android;


import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.google.android.gcm.GCMRegistrar;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.async.AddUpdateEntityTask;
import com.nimbits.android.main.async.LoadMainTask;
import com.nimbits.android.main.async.PostValueTask;
import com.nimbits.android.main.async.SeriesTask;
import com.nimbits.android.startup.async.LoadControlTask;
import com.nimbits.android.ui.PointViewBaseFragment;
import com.nimbits.android.ui.chart.ChartFragment;
import com.nimbits.android.ui.chart.ChartViewActivity;
import com.nimbits.android.ui.dialog.SimpleEntryDialog;
import com.nimbits.android.ui.entitylist.EntityListFragment;
import com.nimbits.android.ui.entitylist.EntityListener;
import com.nimbits.android.ui.point.PointFragment;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeActivity extends Activity implements EntityListener, SeriesTask.SeriesTaskListener {
    public static final String WELCOME = "http://www.nimbits.com/android/welcome.html";


    private PointViewBaseFragment entityFragment;
    private ChartFragment chartFragment;
    AsyncTask<Void, Void, Void> mRegisterTask;

    private static final String TAG = "HomeActivity";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.home_activity_layout);
        if (savedInstanceState == null) {
            loadWelcome();
            startGcm();
            LoadControlTask loadControlTask = new LoadControlTask();
            loadControlTask.execute();
            showEntityFragment();
            if (chartFragment != null && ContentProvider.getCurrentEntity().getEntityType().equals(EntityType.point)) {
                showChartFragment();
            }
        }

    }

    private void loadWelcome() {
        WebView view = (WebView) findViewById(R.id.webView);
        if (view != null) {
            view.loadUrl(WELCOME);
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "resume");
        super.onResume();
        startGcm();



    }
    private void showPointFragment() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);
        frame.removeAllViews();
        entityFragment =  new PointFragment();
        entityFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.main_frame, entityFragment).commit();
    }

    private void showEntityFragment() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);
        frame.removeAllViews();
        entityFragment =  new EntityListFragment();
        entityFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.main_frame, entityFragment).commit();
        if (ContentProvider.getTree().isEmpty()) {
            loadTree();
        }
    }
    private void showChartFragment() {
        Log.v(TAG, "showChartFragment");
        WebView webView = (WebView) findViewById(R.id.webView);
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }

        FrameLayout frame = (FrameLayout) findViewById(R.id.data_frame);
        frame.removeAllViews();
        chartFragment =  new ChartFragment(this);
        chartFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.data_frame, chartFragment).commit();

        //chartFragment.getSeries(entity);
    }

    private void loadTree() {
        if (ContentProvider.currentEntity == null) {
            ContentProvider.currentEntity = Nimbits.session;
        }


        final LoadMainTask task = new LoadMainTask();
        task.setListener(new LoadMainTask.LoadListener() {

            @Override
            public void onSuccess(List<Entity> response) {
                Log.v(TAG, "Loaded " + response.size() + " entities");
                ContentProvider.setTree(response);
                entityFragment.showEntity(getApplicationContext());

            }

            @Override
            public void onProgress(int progress) {

            }


        });
        task.execute();
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
        // Handle presses on the action bar items
        SimpleEntryDialog dialog;
        switch (item.getItemId()) {
            case R.id.action_new_folder:
                dialog = new SimpleEntryDialog(ContentProvider.currentEntity, EntityType.category, Action.create, "Create new Folder");
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_new_point:
                dialog = new SimpleEntryDialog(ContentProvider.currentEntity, EntityType.point, Action.create, "Create new data point");
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_expand:
                if (ContentProvider.currentEntity != null && ContentProvider.currentEntity.getEntityType().equals(EntityType.point)) {
                    String uuid = ContentProvider.currentEntity.getUUID();
                    final SharedPreferences settings =  getSharedPreferences(getString(R.string.app_name), 0);
                    final String base_url = settings.getString(getString(R.string.base_url_setting), getString(R.string.base_url));
                   // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(base_url + "/report.html?uuid=" + uuid));
                    Intent intent = new Intent(getApplicationContext(), ChartViewActivity.class);
                    Bundle b = new Bundle();

                    intent.putExtras(b);
                    startActivity(intent);
                  //  finish();
                }
            case R.id.action_refresh:
                showEntityFragment();
                loadWelcome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onEntityClicked(Entity entity, boolean checkChildren) {
        ContentProvider.setCurrentEntity(entity);

        List<Entity> children;

        if (checkChildren) {
            children = ContentProvider.getChildEntities();
        }
        else {
            children = Collections.emptyList();
        }

        switch (entity.getEntityType()) {

            case user:
                break;
            case point:
                if (children.isEmpty()) {
                    Log.v(TAG, "onSingleEntitySelected");
                    showPointFragment();
                }
                else {
                    showEntity();
                }
                showChartFragment();
                break;
            case category:

                showEntity();
                break;
            case subscription:
                break;
            case calculation:
                break;
            case summary:
                break;
            case accessKey:
                break;
        }
    }

    private void showEntity() {
        if (entityFragment != null) {
            entityFragment.showEntity(getApplicationContext());
        }
    }


    @Override
    public void onNewEntity(Entity parent, EntityType type, EntityName name) {
        Entity entity = EntityModelFactory.createEntity(name, type);
        entity.setOwner(Nimbits.session.getOwner());
        entity.setParent(parent.getKey());

        AddUpdateEntityTask.getInstance(new AddUpdateEntityTask.AddUpdateEntityTaskListener() {

            @Override
            public void onSuccess(List response) {
                if (response.isEmpty()) {
                    ToastHelper.show(getApplicationContext(), "something went wrong...");
                } else {
                    ContentProvider.addEntities(response);
                    showEntityFragment();
                }
            }

            @Override
            public void onFail(Exception ex) {
                ToastHelper.show(getApplicationContext(), ex.getMessage());
            }
        }).execute(entity, EntityModel.class, true);
    }

    @Override
    public void onValueUpdated(Entity entity, Value response) {

        Log.v(TAG, "Home Activity Updating list on new value");
        showEntity();


    }

    @Override
    public void onNewValue(final Entity entity, final String entry) {
        InputMethodManager inputManager = (InputMethodManager)
                getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        Value value = ValueFactory.createValueFromString(SimpleValue.getInstance(entry), new Date());
        PostValueTask.getInstance(new PostValueTask.PostValueTaskListener() {
            @Override
            public void onSuccess(List<Value> response) throws Exception {
                Log.v(TAG, "Value Recorded");
                if (! response.isEmpty()) {
                    ContentProvider.updateCurrentValue(entity, response.get(0));

                }
            }

            @Override
            public void onFail(Exception ex) {
                Log.v(TAG, ex.getMessage());
                ToastHelper.show(getApplication().getApplicationContext(), "Something went wrong:"  + ex.getMessage());
            }
        }).execute(entity, value);
    }

    @Override
    public void newValuePrompt(Entity entity) {
        SimpleEntryDialog dialog  = new SimpleEntryDialog(entity, EntityType.category,
                Action.recordValue, entity.getName().getValue() +
                ": Enter Value");
        dialog.show(getFragmentManager(), "NoticeDialogFragment");

    }




    @Override
    public void onBackPressed() {
        goBack();

    }
    private void goBack() {
        if (ContentProvider.getCurrentEntity().getEntityType().equals(EntityType.user)) {
            finish();
        }
        else {
            ContentProvider.setCurrentEntityToParent();
            showEntityFragment();
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
        GCMRegistrar.onDestroy(this);
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

    //end gcm stuff
}