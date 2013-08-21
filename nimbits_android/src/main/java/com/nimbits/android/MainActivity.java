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
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.async.delete.DeleteEntityDialogFragment;
import com.nimbits.android.ui.entitylist.EntityListFragment;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;

import java.util.Timer;

/**
 * User: benjamin
 * Date: 12/27/12
 * Time: 7:52 PM
 */
public class MainActivity extends Activity
{

    private Context context;


    private EntityListFragment entityListFragment;



    private final Timer timer = new Timer();
    private TextView titleView;

    private ProgressBar progressBar;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.main_activity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        titleView = (TextView) findViewById(R.id.entity_name);
        titleView.setText(Nimbits.session.getEmail().getValue());



    }




    //    @SuppressWarnings("unchecked")
//    private void loadCurrentValues() {
//        final View current = stack.getCurrentView();
//
//        if (current != null) {
//            ListView entityList = (ListView) current.findViewById(R.id.entity_list_view);
//
//            if (entityList != null && entityList.getAdapter() != null) {
//                Log.v("nimbits", "running loadcurrent" + entityList.getChildCount());
//
//                Log.v("nimbits", String.valueOf(entityList.getAdapter().getCount()));
//                for (int i = 0; i < entityList.getChildCount(); i++) {
//
//                    Entity entity = (Entity) entityList.getAdapter().getItem(i);
//                    final View view = entityList.getChildAt(i);
//
//                    final TextView valueText = (TextView) view.findViewById(R.id.value);
//                    final TextView note = (TextView) view.findViewById(R.id.note);
//                    final ImageView status = (ImageView) view.findViewById(R.id.entity_image);
//                    final TextView timestamp = (TextView) view.findViewById(R.id.timestamp);
//                    Log.v("nimbits", "got field" + valueText.getText());
//
//                    if (entity.getEntityType().equals(EntityType.point)) {
//                        // TextView valueEntry = (TextView) findViewById(R.id.current_value);
//
//
//                        LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
//                            @Override
//                            public void onSuccess(Value response) {
//                                PointViewHelper.setViews(response, valueText, timestamp, note, status, SimpleValue.getEmptyInstance());
//
//                            }
//                        }).execute(entity);
//                    } else {
//
//                        valueText.setVisibility(View.GONE);
//                    }
//
//
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void onPositiveDeleteEntityButtonClicked() {
//
//        DeleteEntityTask.getInstance(new DeleteEntityTask.DeleteEntityTaskListener() {
//            @Override
//            public void onSuccess() {
//              //  goBack();
//            }
//        });
//
//    }





    private class MainToolBar {





        private void initDeleteButton() {
            ImageButton delete = (ImageButton) findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (! ContentProvider.currentEntity.getEntityType().equals(EntityType.user)) {
                        DeleteEntityDialogFragment delete = new DeleteEntityDialogFragment(getBaseContext());
                        delete.show(getFragmentManager(), "Confirm");
                    }
                    else {
                        ToastHelper.show(getApplicationContext(), "Select the object you'd like to delete first, you're on the top level.");
                    }

                }
            });
        }









    }







}
