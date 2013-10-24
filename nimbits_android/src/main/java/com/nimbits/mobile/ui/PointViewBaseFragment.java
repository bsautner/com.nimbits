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

package com.nimbits.mobile.ui;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.mobile.R;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.dao.ApplicationDao;
import com.nimbits.mobile.dao.ApplicationDaoFactory;
import com.nimbits.mobile.dao.orm.TreeTable;
import com.nimbits.mobile.main.async.LoadValueTask;
import com.nimbits.mobile.ui.entitylist.EntityListener;

import java.util.*;


public class PointViewBaseFragment extends Fragment {
    private final static String TAG = "BaseEntityFragment";
    private Timer timer;


    protected View view;
    protected EntityListener listener;
    protected ApplicationDao dao;
    public PointViewBaseFragment(EntityListener listener) {
        this.listener = listener;

    }
    public PointViewBaseFragment( ) {


    }
    @Override
    public void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        showTitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        if (this.timer != null) {
            Log.v(TAG, "onPause stopping timer");
            this.timer.cancel();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (EntityListener) activity;


    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        if (this.timer != null) {
            Log.v(TAG, "onStop stopping timer");
            this.timer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();

    }

    public void showTitle() {
        this.dao = ApplicationDaoFactory.getInstance();
        this.timer = new Timer();
        Entity entity = SessionSingleton.getInstance().getCurrentEntity();
        final TextView currentValue = (TextView) view.findViewById((R.id.value));
        final TextView timestamp = (TextView) view.findViewById((R.id.timestamp));
        final ImageView entityImage = (ImageView) view.findViewById(R.id.entity_image);
        if (entity.getEntityType().equals(EntityType.point)) {
            Value value = dao.getValue(SessionSingleton.getInstance().getCurrentEntityPK());
            setImage(entityImage, entity.getEntityType(), value.getAlertState());

        }
        else {
            setImage(entityImage, entity.getEntityType(), AlertType.OK);
            currentValue.setVisibility(View.GONE);
            timestamp.setVisibility(View.GONE);
        }

        TextView name = (TextView) view.findViewById(R.id.entity_name);

        name.setText(entity.getName().getValue());



        this.timer.cancel();
        this.timer = new Timer();
        TimerTask updateTask;
        updateTask = new UpdateValuesTask(this.listener, dao, entity);
        this.timer.scheduleAtFixedRate(updateTask, 0, SessionSingleton.getInstance().getControl().getTimer());

    }
    protected void setImage(ImageView view, EntityType type, AlertType state) {
        view.setVisibility(View.VISIBLE);
        switch (type) {

            case calculation:

                view.setImageDrawable(getResources().getDrawable(R.drawable.calc));
                break;
            case category:
                view.setImageDrawable(getResources().getDrawable(R.drawable.folder));
                break;
            case user:
                view.setVisibility(View.GONE);
                break;
            case point:

                switch (state) {

                    case LowAlert:
                        view.setImageResource(R.drawable.bluestar);
                        break;
                    case HighAlert:
                        view.setImageResource(R.drawable.redstar);
                        break;
                    case IdleAlert:
                        view.setImageResource(R.drawable.yellowstar);
                        break;
                    case OK:
                        view.setImageResource(R.drawable.greenstar);
                        break;
                }
                break;
        }
    }
    public void refreshData() {
        showTitle();
    }

    static class UpdateValuesTask extends TimerTask {
        EntityListener listener;

        ApplicationDao dao;
        Entity entity;
        UpdateValuesTask(EntityListener listener, ApplicationDao dao, Entity entity) {
            this.listener = listener;

            this.entity = entity;
            this.dao = dao;
        }


        public void run() {

            Entity e = SessionSingleton.getInstance().getCurrentEntity();
            Cursor cursor = null;
            try {
                cursor = dao.getChildren(e);
                long id;
                int type;
                Map<Long, Entity> list = new HashMap<Long, Entity>(cursor.getCount());
                while (cursor.moveToNext()) {

                    id = cursor.getLong(cursor.getColumnIndex(TreeTable.getId()));
                    type =  cursor.getInt(cursor.getColumnIndex(TreeTable.getType()));
                    EntityType entityType = EntityType.get(type);
                    if (entityType != null && entityType.recordsData()) {
                        e = dao.getEntity(id);
                        list.put(id, e);
                    }


                    //  Log.v(TAG, cursor.getString(0));

                }
                if (entity.getEntityType().recordsData()) {
                    list.put(SessionSingleton.getInstance().getCurrentEntityPK(), entity);
                }
                Log.v(TAG, "timer tick: " + list.size());
                final Random r = new Random();
                for (final long l : list.keySet()) {
                    final Entity em = list.get(l);
                    LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
                        @Override
                        public void onSuccess(Value response) {
                            Log.v(TAG, response.toString());

                            dao.updateValue(l, ValueFactory.createValueModel(r.nextDouble()));
                            listener.onValueUpdated(entity.getKey(), response);
                        }
                    }).execute(em);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }


        }
    }


}
