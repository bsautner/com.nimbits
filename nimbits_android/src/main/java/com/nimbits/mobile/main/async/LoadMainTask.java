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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.dao.ApplicationDaoFactory;
import com.nimbits.transaction.TransactionImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 8:26 AM
 */
public class LoadMainTask extends AsyncTask<Object, Integer, List<Entity>> {


    private LoadListener mListener;
    private final Context context;
    public interface LoadListener {
        public void onSuccess(List<Entity> response);

        public void onProgress(int progress);

    }

    public void setListener(LoadListener listener) {
        mListener = listener;
    }

    public LoadMainTask(final Context contex) {
       this.context = contex;
    }


    @Override
    protected List<Entity> doInBackground(Object... objects) {

        publishProgress(10);
        Server url = SessionSingleton.getInstance().getServer() ;

        List<Entity> response;

        response = ApplicationDaoFactory.getInstance(context).getTree(SessionSingleton.getInstance().getServer().getId());
        if (response.isEmpty()) {
            response = new TransactionImpl(url, SessionSingleton.getInstance().getEmail()).getTree();
            if (! response.isEmpty()) {
                ApplicationDaoFactory.getInstance(context).storeTree(SessionSingleton.getInstance().getServer().getId(), response);
            }
            else {
                response = Collections.emptyList();
            }
        }


        List<Entity> retObj = new ArrayList<Entity>(response.size());
        for (Entity e : response) {
            if (e.getEntityType().isAndroidReady()) {
                retObj.add(e);
            }
        }

        publishProgress(100);
        return retObj;

    }

    @Override
    protected void onPostExecute(List<Entity> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mListener.onProgress(progress[0]);
        Log.v("nimbits", "progress update");
    }
}