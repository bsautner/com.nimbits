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

package com.nimbits.mobile.main.async.delete;

import android.os.AsyncTask;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.transaction.Transaction;
import com.nimbits.transaction.TransactionFactory;

/**
 * Author: Benjamin Sautner
 * Date: 1/15/13
 * Time: 11:31 AM
 */
public class DeleteEntityTask extends AsyncTask<Object, Void, Void> {

    public static DeleteEntityTaskListener mListener;
    private Transaction transactions = TransactionFactory.getInstance(SessionSingleton.getInstance().getServer(), SessionSingleton.getInstance().getEmail());

    private DeleteEntityTask() {

    }

    public static DeleteEntityTask getInstance(DeleteEntityTaskListener listener) {
        DeleteEntityTask instance = new DeleteEntityTask();
        instance.setListener(listener);
        return instance;

    }

    public interface DeleteEntityTaskListener {
        public void onSuccess();

    }


    private void setListener(DeleteEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Entity e = (Entity) objects[0];

        transactions.deleteEntity(e);


        return null;

    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Void response) {
        super.onPostExecute(response);
        mListener.onSuccess();
    }

} 
 
  