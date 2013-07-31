package com.nimbits.android;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gcm.GCMRegistrar;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.ui.chart.ChartFragment;
import com.nimbits.android.ui.entitylist.EntityListFragment;
import com.nimbits.android.main.async.AddUpdateEntityTask;
import com.nimbits.android.main.async.LoadMainTask;
import com.nimbits.android.ui.entitylist.EntityListener;
import com.nimbits.android.ui.dialog.EntityNameDialog;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;

import java.util.List;

public class HomeActivity extends ActionBarActivity implements EntityListener {
    private ProgressBar progressBar;
    private EntityListFragment entityListFragment;
    private ChartFragment chartFragment;
    AsyncTask<Void, Void, Void> mRegisterTask;

    private static final String TAG = "HomeActivity";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState == null) {

            showEntityFragment();
            startGcm();
        }
        //   else {
        //     Log.v(TAG, (entityListFragment == null) + " " +  ContentProvider.tree.size());
        //  }

    }

    private void showEntityFragment() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);
        frame.removeAllViews();
        entityListFragment =  EntityListFragment.getInstance(this);
        entityListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, entityListFragment).commit();
        if (ContentProvider.getTree().isEmpty()) {
            loadTree();
        }
//        else {
//            entityListFragment.showEntity(getApplicationContext());
//        }
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
                entityListFragment.showEntity(getApplicationContext());
            }

            @Override
            public void onProgress(int progress) {
                updateProgressBar(progress);
            }


        });
        task.execute();
    }

    private void updateProgressBar(int progress) {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setMax(100);
        bar.setProgress(progress);

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
        EntityNameDialog dialog;
        switch (item.getItemId()) {
            case R.id.action_new_folder:
                dialog = new EntityNameDialog(ContentProvider.currentEntity, EntityType.category);
                dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_new_point:
                dialog = new EntityNameDialog(ContentProvider.currentEntity, EntityType.point);
                dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onEntityClicked(Entity entity) {
        switch (entity.getEntityType()) {

            case user:
                break;
            case point:
                showChartFragment(entity);
                break;
            case category:

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

    private void showChartFragment(Entity entity) {
        FrameLayout frame = (FrameLayout) findViewById(R.id.data_frame);
        frame.removeAllViews();
        chartFragment =  ChartFragment.getInstance(this);
        chartFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.data_frame, chartFragment).commit();
        //chartFragment.getSeries(entity);
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

    @Override
    protected void onResume() {
        Log.v(TAG, "resume");
        super.onResume();
        startGcm();
        if (chartFragment != null && ContentProvider.getCurrentEntity().getEntityType().equals(EntityType.point)) {
            showChartFragment(ContentProvider.getCurrentEntity());
        }
    }



    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
                    //mDisplay.append(newMessage + "\n");
                }
            };

    //end gcm stuff
}