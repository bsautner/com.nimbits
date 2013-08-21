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

package com.nimbits.android.ui.point;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.nimbits.android.R;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.ui.PointViewBaseFragment;
import com.nimbits.android.ui.entitylist.EntityListener;

/**
 * Created by benjamin on 8/8/13.
 */
public class PointFragment extends PointViewBaseFragment {
    private final static  String TAG = "PointFragment";


    @SuppressWarnings("unused")
    public PointFragment() {
        super();
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (EntityListener) activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        view = inflater.inflate(R.layout.point_details_fragment, container, false);
        showEntity(getActivity());

        ImageButton saveButton = (ImageButton) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) view.findViewById(R.id.new_value);
                if (editText != null && editText.getEditableText() != null) {
                    String value = editText.getEditableText().toString();
                    listener.onNewValue(ContentProvider.getCurrentEntity(), value);
                    editText.getEditableText().clear();
                }

            }
        });


        return view;
    }




    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
