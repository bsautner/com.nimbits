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

package com.nimbits.android.main.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.commons.lang3.Range;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 6:12 PM
 */
public class SeriesTask extends AsyncTask<Object, List<Value>, List<Value>> {

    public static SeriesTaskListener mListener;

    private SeriesTask() {

    }

    public static SeriesTask getInstance(SeriesTaskListener listener) {
        SeriesTask instance = new SeriesTask();
        instance.setListener(listener);
        return instance;

    }

    public interface SeriesTaskListener {
        public void onSuccess(List<Value> response);

        // @SuppressWarnings("unused")
        //public void onFail(Exception ex);

    }


    private void setListener(SeriesTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Value> doInBackground(Object... objects) {

        Entity entity = (Entity) objects[0];
        Range<Integer> range = (Range<Integer>) objects[1];

            return Transaction.getSeries(entity, range);



    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(List<Value> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  