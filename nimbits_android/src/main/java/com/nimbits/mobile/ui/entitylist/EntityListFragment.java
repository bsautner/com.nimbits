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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.nimbits.mobile.R;
import com.nimbits.mobile.content.ContentProvider;
import com.nimbits.mobile.ui.PointViewBaseFragment;

/**
 * @Author: benjamin
 */
public class EntityListFragment extends PointViewBaseFragment {
    public final static String TAG = "EntityListFragment";

    private ListView list;


    private Context context;
    private EntityListAdapter adapter;

    @SuppressWarnings("unused")
    public EntityListFragment() {
        super();

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.entity_list_fragment, container, false);
        ProgressBar progressBar = null;
        if (view != null) {
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        Log.v(TAG, "view created " + (adapter == null));

        showEntity(getActivity());

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        showEntity(context);

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    public void showEntity(Context context) {
        super.showEntity(context);
        this.context = context;
        Log.v(TAG, "showEntity" + (context == null));
        adapter = new EntityListAdapter(context, R.id.listView, ContentProvider.getChildEntities());
        adapter.setEntityListener(listener);
        list = (ListView) view.findViewById(R.id.listView);
        list.setAdapter(adapter);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (bar != null) {
            bar.setVisibility(View.GONE);
        }


    }


}
