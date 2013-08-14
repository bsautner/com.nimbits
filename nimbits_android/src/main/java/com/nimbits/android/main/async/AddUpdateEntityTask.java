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
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 12:58 PM
 */
public class AddUpdateEntityTask extends AsyncTask<Object, List<Entity>, List<Entity>> {

    private AddUpdateEntityTask() {

    }

    private static AddUpdateEntityTaskListener mListener;

    public static AddUpdateEntityTask getInstance(AddUpdateEntityTaskListener listener) {
        AddUpdateEntityTask instance = new AddUpdateEntityTask();
        instance.setListener(listener);
        return instance;

    }


    public interface AddUpdateEntityTaskListener<T> {
        public void onSuccess(List<T> response);

        @SuppressWarnings("unused")
        public void onFail( Exception ex);

    }

    public void setListener(AddUpdateEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Entity> doInBackground(Object... objects) {
         Entity e = (Entity) objects[0];
        Class clz = (Class) objects[1];
        boolean isNew = (Boolean) (objects[2]);

            if (isNew) {

                return Transaction.addEntity(e, clz);

            } else {
                return Transaction.updateEntity(e, clz);
            }




    }

    @Override
    protected void onPostExecute(List<Entity> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  