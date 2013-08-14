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


import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/13/13
 * Time: 9:55 AM
 */



public class PostValueTask extends AsyncTask<Object, List<Value>, List<Value>> {

    public static PostValueTaskListener mListener;

    private PostValueTask() {

    }

    public static PostValueTask getInstance(PostValueTaskListener listener) {
        PostValueTask instance = new PostValueTask();
        instance.setListener(listener);
        return instance;

    }

    public interface PostValueTaskListener {
        public void onSuccess(List<Value> response) throws Exception;

        @SuppressWarnings("unused")
        public void onFail(Exception ex);

    }


    private void setListener(PostValueTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Value> doInBackground(Object... objects) {
        List<Value> response;
        Entity entity = (Entity) objects[0];
        Value value = (Value) objects[1];

            response = Transaction.postValue(entity, value);

        return response;


    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(List<Value> ts) {
        super.onPostExecute(ts);
        try {
            mListener.onSuccess(ts);
        } catch (Exception e) {
            mListener.onFail(e);


        }
    }

} 
 
  