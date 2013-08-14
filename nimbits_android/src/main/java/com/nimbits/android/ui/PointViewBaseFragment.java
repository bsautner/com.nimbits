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

package com.nimbits.android.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nimbits.android.R;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.PointViewHelper;
import com.nimbits.android.main.async.LoadValueTask;
import com.nimbits.android.ui.entitylist.EntityListener;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by benjamin on 8/8/13.
 */
public class PointViewBaseFragment extends Fragment {
    private final static String TAG = "PointViewBaseFragment";
    private Timer timer;

    protected View view;
    protected EntityListener listener;
    public PointViewBaseFragment() {
    }

    public void setListener(EntityListener listener) {
        this.listener = listener;
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
        if (this.timer != null) {
            timer.cancel();
            TimerTask updateTask;
            updateTask = new UpdateValuesTask(this.listener);
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(updateTask, 0, Nimbits.getControl().getTimer());

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        this.timer = new Timer();

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


    static class UpdateValuesTask extends TimerTask {
        EntityListener listener;

        UpdateValuesTask(EntityListener listener) {
            this.listener = listener;
        }



        public void run() {
            Log.v(TAG, "timer tick");
            List<Entity> children = ContentProvider.getChildEntities();
            List<Entity> refresh = new ArrayList<Entity>(children.size() +1);
            if (ContentProvider.getCurrentEntity().getEntityType().equals(EntityType.point)) {
                refresh.add(ContentProvider.getCurrentEntity());
            }
            for (Entity e : children) {
                if (e.getEntityType().equals(EntityType.point)) {
                    refresh.add(e);
                }
            }

//            refresh.add(ContentProvider.getCurrentEntity());
            for (final Entity entity : refresh) {
                Log.v(TAG, entity.getName().getValue());
                LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
                    @Override
                    public void onSuccess(Value response) {
                        ContentProvider.updateCurrentValue(entity, response);

                        listener.onValueUpdated(entity, response);
                    }
                }).execute(entity);
            }



        }
    }

    public void showEntity(Context context) {
        TextView name = (TextView) view.findViewById(R.id.entity_name);
        name.setText(ContentProvider.currentEntity.getName().getValue());
        final TextView currentValue = (TextView) view.findViewById((R.id.value));
        final TextView timestamp = (TextView) view.findViewById((R.id.timestamp));
        final ImageView entityImage = (ImageView) view.findViewById(R.id.entity_image);
        currentValue.setVisibility(View.VISIBLE);
        timestamp.setVisibility(View.VISIBLE);
        PointViewHelper.setViews(ContentProvider.getCurrentValue(ContentProvider.currentEntity), currentValue, timestamp, entityImage, SimpleValue.getEmptyInstance());
        LinearLayout mainView = (LinearLayout) view.findViewById(R.id.entity);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEntityClicked(ContentProvider.getCurrentEntity(), false);
            }
        });
    }
}
