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


import android.util.Log;

import java.util.TimerTask;

/**
 * Author: Benjamin Sautner
 * Date: 1/15/13
 * Time: 10:32 AM
 */
public class DataPollingTask extends TimerTask {

    public static DataPollingTaskListener mListener;

    private DataPollingTask() {

    }

    @Override
    public void run() {
        Log.v("nimbits", "tick");
        mListener.onSuccess();
    }

    public static DataPollingTask getInstance(DataPollingTaskListener listener) {
        DataPollingTask instance = new DataPollingTask();
        instance.setListener(listener);
        return instance;

    }

    public interface DataPollingTaskListener {
        public void onSuccess();

    }

    private void setListener(DataPollingTaskListener listener) {
        mListener = listener;
    }


}
 
  