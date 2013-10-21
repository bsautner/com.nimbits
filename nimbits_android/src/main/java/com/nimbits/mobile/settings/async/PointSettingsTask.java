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

import android.os.AsyncTask;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.helper.PointHelper;
import com.nimbits.mobile.application.SessionSingleton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 11:59 AM
 */
public class PointSettingsTask extends AsyncTask<Object, List<Point>, List<Point>> {
    private static PointSettingsTaskListener mListener;

    private PointSettingsTask() {

    }

    public static PointSettingsTask getInstance(PointSettingsTaskListener listener) {
        final PointSettingsTask instance = new PointSettingsTask();
        instance.setListener(listener);
        return instance;


    }


    public interface PointSettingsTaskListener {
        public void onSuccess(List<Point> response);


    }

    private void setListener(PointSettingsTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Point> doInBackground(Object... objects) {

        Entity e = (Entity) objects[0];
        try {
            Server url = SessionSingleton.getInstance().getServer();
            String email = SessionSingleton.getInstance().getEmail();
            return Arrays.asList(new PointHelper(url, email).getPoint(e.getName().getValue()));

        } catch (Exception e1) {
            return Collections.emptyList();


        }
    }

    @Override
    protected void onPostExecute(List<Point> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  