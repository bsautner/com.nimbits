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

package com.nimbits.mobile.startup.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;


public class StartupTask extends AsyncTask<Object, User, List<User>> {

    private StartupListener mListener;

    public interface StartupListener {
        public void onLoginSuccess(List<User> response);

        public void onLoginFail();

    }

    private void setListener(StartupListener listener) {
        mListener = listener;
    }

    public static StartupTask getInstance(StartupListener listener) {
        StartupTask task = new StartupTask();
        task.setListener(listener);
        return task;
    }

    @Override
    protected List<User> doInBackground(Object... o) {


        return Transaction.getSession();

    }

    @Override
    protected void onPostExecute(List<User> response) {
        super.onPostExecute(response);

        if (response.isEmpty()) {
            mListener.onLoginFail();
        } else {
            mListener.onLoginSuccess(response);
        }

    }

}
