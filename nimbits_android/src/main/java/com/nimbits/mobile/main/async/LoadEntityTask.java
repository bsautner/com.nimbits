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

package com.nimbits.mobile.main.async;

import android.os.AsyncTask;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.transaction.Transaction;
import com.nimbits.transaction.TransactionFactory;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 2:12 PM
 */
public class LoadEntityTask<T> extends AsyncTask<Object, List<T>, List<T>> {
    private Transaction transactions = TransactionFactory.getInstance(SessionSingleton.getInstance().getServer(), SessionSingleton.getInstance().getEmail());

    private LoadEntityTask() {

    }

    public static LoadEntityTask getInstance(LoadPointTaskListener listener) {
        LoadEntityTask instance = new LoadEntityTask();
        instance.setListener(listener);
        return instance;
    }


    private LoadPointTaskListener<T> mListener;

    public interface LoadPointTaskListener<T> {
        public void onSuccess(List<T> response);

    }

    private void setListener(LoadPointTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<T> doInBackground(Object... objects) {
        List<T> response;
        Entity entity = (Entity) objects[0];
        Class clz = (Class) objects[1];

        response = (List<T>) transactions.getEntity(SimpleValue.getInstance(entity.getKey())
                , entity.getEntityType(), clz);

        return response;


    }

    @Override
    protected void onPostExecute(List<T> ts) {
        super.onPostExecute(ts);

        mListener.onSuccess(ts);

    }

} 
 
  