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

package com.nimbits.mobile.server;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

public class BufferService extends Service {

    public final static String TAG = "BufferService";



    public BufferService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");



        return START_STICKY;

    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate();





    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();

    }

    public IBinder onBind(Intent intent) {
        return null;
    }









}
