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

package com.nimbits.mobile.ui.entitylist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.mobile.R;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.dao.orm.TreeTable;
import com.nimbits.mobile.ui.PointViewBaseFragment;

import java.util.Date;


public class EntityListFragment extends PointViewBaseFragment {
    public final static String TAG = "ListEntityFragment";

    private Context context;
    SimpleCursorAdapter adapter;

    @SuppressWarnings("unused")
    public EntityListFragment() {
        super();

    }


    public EntityListFragment(EntityListener listener) {
        super(listener);
        this.listener = listener;
    }

//    @Override
//    public void refresh() {
//        super.refresh();
//        //loadData();
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        view = inflater.inflate(R.layout.entity_list_fragment, container, false);



        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume" + (context == null));
        show();


    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();


    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void refreshData() {
        super.refreshData();
        Entity e = SessionSingleton.getInstance().getCurrentEntity();
        Cursor cursor = dao.getChildren(e);
        adapter.changeCursor(cursor);

    }


    public void show( ) {
        super.showTitle();
        this.context = getActivity();
        Entity e = SessionSingleton.getInstance().getCurrentEntity();
        Cursor cursor = dao.getChildren(e);

        Log.v(TAG, "showEntity" + (context == null) + " " + cursor.getCount());
        String[] columns = new String[] {TreeTable.getType(), TreeTable.getName(), TreeTable.getState(), TreeTable.getValue(), TreeTable.getTimestamp() };
        int[] to = new int[] {R.id.entity_image,  R.id.entity_name, R.id.status, R.id.value, R.id.timestamp};

        adapter = new SimpleCursorAdapter(context, R.layout.entity_list_item, cursor, columns, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor theCursor, int columnIndex) {



                if (view.getId() == R.id.entity_image ) {
                    int typeIndex = theCursor.getColumnIndex(TreeTable.getType());
                    int typeValue = theCursor.getInt(typeIndex);
                    EntityType type = EntityType.get(typeValue);

                    if (type != null) {
                        int s = theCursor.getColumnIndexOrThrow(TreeTable.getState());
                        int si = theCursor.getInt(s);
                        AlertType state = AlertType.get(si);
                        setImage((ImageView) view, type, state);
                    }
                } else if (view.getId() == R.id.entity_name) {
                    int nameIndex = theCursor.getColumnIndexOrThrow(TreeTable.getName());
                    TextView textView = (TextView) view;
                    String name = theCursor.getString(nameIndex);
                    ((TextView) view).setText(name);

                }
                else if (view.getId() == R.id.value) {
                    TextView t = (TextView) view;
                   // if (type != null && type.equals(EntityType.point)) {
                        String v = theCursor.getString(theCursor.getColumnIndex(TreeTable.getValue()));

                        if (v != null) {
                            t.setVisibility(View.VISIBLE);
                            t.setText(v);
                        }  else {

                            t.setVisibility(View.GONE);
                        }

                    }

                else if (view.getId() == R.id.timestamp) {
                    TextView t = (TextView) view;

                        t.setVisibility(View.VISIBLE);
                        long v = theCursor.getLong(theCursor.getColumnIndex(TreeTable.getTimestamp()));
                        if (v > 0) {
                            String time = (String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date(v));

                            t.setText(time);
                        }
                        else {
                            t.setVisibility(View.GONE);
                        }


                }
                return true;

            }

        });



        ListView list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SessionSingleton.getInstance().setCurrentEntity(id);
                Entity entity = SessionSingleton.getInstance().getCurrentEntity();
                refreshData();
                listener.onEntityClicked(entity);

            }
        });



    }




}
