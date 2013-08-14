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

package com.nimbits.android.settings.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 12:36 PM
 */
public class UpdateEntityTask<T> extends AsyncTask<Object, List<T>, List<T>> {

    private UpdateEntityTask() {

    }

    public static UpdateEntityTask getInstance(UpdateEntityTaskListener listener) {
        final UpdateEntityTask instance = new UpdateEntityTask();
        instance.setListener(listener);
        return instance;


    }


    private UpdateEntityTaskListener<T> mListener;

    public interface UpdateEntityTaskListener<T> {
        public void onSuccess(List<T> response);

        @SuppressWarnings("unused")
        public void onFail(Exception ex);

    }

    public void setListener(UpdateEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<T> doInBackground(Object... objects) {
        List<T> response;
        Entity e = (Entity) objects[0];
        Class clz = (Class) objects[1];

            response = (List<T>) Transaction.updateEntity(e, clz);

        return response;


    }

    @Override
    protected void onPostExecute(List<T> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  