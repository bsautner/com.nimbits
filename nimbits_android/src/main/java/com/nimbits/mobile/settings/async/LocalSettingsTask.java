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

package com.nimbits.mobile.settings.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.nimbits.mobile.R;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.simple.SimpleValue;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 9:04 AM
 */
public class LocalSettingsTask extends AsyncTask<Object, SimpleValue<String>, SimpleValue<String>> {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private LocalSettingsTaskListener listener;

    private LocalSettingsTask() {

    }


    public static LocalSettingsTask getInstance() {
        LocalSettingsTask instance = new LocalSettingsTask();

        return instance;

    }

    public interface LocalSettingsTaskListener {
        void onRead(SimpleValue<String> response);
    }

    public static LocalSettingsTask getInstance(LocalSettingsTaskListener listener) {
        LocalSettingsTask instance = new LocalSettingsTask();
        instance.setListener(listener);
        return instance;

    }

    private void setListener(LocalSettingsTaskListener listener) {
        this.listener = listener;
    }


    @Override
    protected SimpleValue<String> doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        Action action = (Action) objects[1];
        Parameters name = (Parameters) objects[2];


        settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
        if (action.equals(Action.create)) {
            SimpleValue<String> value = (SimpleValue<String>) objects[3];


            editor = settings.edit();
            editor.putString(name.getText(), value.toString());
            editor.commit();
            return value;

        } else {
            String sample = settings.getString(name.getText(), "");

            SimpleValue<String> response = SimpleValue.getInstance(sample);
            listener.onRead(response);
            return response;

        }
    }

}

 
  