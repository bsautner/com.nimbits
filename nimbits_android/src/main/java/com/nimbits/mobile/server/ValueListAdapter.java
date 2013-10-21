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

package com.nimbits.mobile.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nimbits.client.model.value.ValueContainer;
import com.nimbits.mobile.R;

import java.util.List;

/**
 * Created by benjamin on 9/9/13.
 */
public class ValueListAdapter extends ArrayAdapter<ValueContainer> {

    private List<ValueContainer> items;
    private Context context;


    public ValueListAdapter(Context context, int textViewResourceId, List<ValueContainer> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.items = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;


        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.buffered_value_item, null);

            final ValueContainer item = items.get(position);
            TextView text = (TextView) v.findViewById(R.id.textView);
            text.setText(item.getId() + " " + item.getValue().getValueWithNote());


        }
        return v;
    }
}
