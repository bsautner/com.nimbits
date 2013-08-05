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
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gcm.GCMRegistrar;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.async.PostValueTask;
import com.nimbits.android.startup.async.LoadControlTask;
import com.nimbits.android.ui.chart.ChartFragment;
import com.nimbits.android.ui.dialog.SimpleEntryDialog;
import com.nimbits.android.ui.entitylist.EntityListFragment;
import com.nimbits.android.main.async.AddUpdateEntityTask;
import com.nimbits.android.main.async.LoadMainTask;
import com.nimbits.android.ui.entitylist.EntityListener;
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

import java.util.Date;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements EntityListener {
    public static final String WELCOME = "http://www.nimbits.com/android/welcome.html";

    private EntityListFragment entityListFragment;
    private ChartFragment chartFragment;
    AsyncTask<Void, Void, Void> mRegisterTask;

    private static final String TAG = "HomeActivity";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);


        WebView view = (WebView) findViewById(R.id.webView);
        view.loadUrl(WELCOME);

        if (savedInstanceState == null) {

            showEntityFragment();
            startGcm();
            LoadControlTask loadControlTask = new LoadControlTask();
            loadControlTask.execute();
        }


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
                dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                return true;
            case R.id.action_new_point:
                dialog = new SimpleEntryDialog(ContentProvider.currentEntity, EntityType.point, Action.create, "Create new Folder");
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
        chartFragment =  ChartFragment.getInstance(this, entity);
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
    public void onValueUpdated(Entity entity, Value response) {
           if (entityListFragment != null) {
               Log.v(TAG, "Home Activity Updating list on new value");
               entityListFragment.showEntity(getApplicationContext() );
           }
    }

    @Override
    public void onNewValue(final Entity entity, final String entry) {

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
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");

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