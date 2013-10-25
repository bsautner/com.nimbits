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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.mobile.R;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.ui.entitylist.EntityListener;


public class PointViewBaseFragment extends Fragment {
    private final static String TAG = "BaseEntityFragment";


    protected View view;
    protected EntityListener listener;


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

    }

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach");
        super.onAttach(activity);
        listener = (EntityListener) activity;


    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();

    }

    protected void showTitle() {
        final Entity entity = SessionSingleton.getInstance().getCurrentEntity();
        final TextView currentValue = (TextView) view.findViewById((R.id.value));
        final TextView timestamp = (TextView) view.findViewById((R.id.timestamp));
        final ImageView entityImage = (ImageView) view.findViewById(R.id.entity_image);
        if (entity.getEntityType().equals(EntityType.point)) {
            Value value = SessionSingleton.getInstance().getDao().getValue(SessionSingleton.getInstance().getCurrentEntityPK());
            setImage(entityImage, entity.getEntityType(), value.getAlertState());

        }
        else {
            setImage(entityImage, entity.getEntityType(), AlertType.OK);
            currentValue.setVisibility(View.GONE);
            timestamp.setVisibility(View.GONE);
        }

        TextView name = (TextView) view.findViewById(R.id.entity_name);

        name.setText(entity.getName().getValue());





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




}
