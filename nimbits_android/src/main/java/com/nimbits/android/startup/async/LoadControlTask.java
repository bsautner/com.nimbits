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

package com.nimbits.android.startup.async;

import android.os.AsyncTask;
import android.util.Log;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.android.AndroidControl;
import com.nimbits.cloudplatform.client.android.AndroidControlFactory;
import com.nimbits.cloudplatform.client.android.AndroidControlImpl;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 8:26 AM
 */
public class LoadControlTask extends AsyncTask<Object, Integer, AndroidControl> {




    public LoadControlTask() {

    }


    @Override
    protected AndroidControl doInBackground(Object... objects) {



        List<AndroidControl> control =  Transaction.getControl();
        if (control.isEmpty()) {
            Nimbits.setControl(AndroidControlFactory.getConservativeInstance()); //load highly conservative values since something is wrong.
        }
        else {
        Nimbits.setControl(control.get(0));
        }
        return  Nimbits.getControl();

    }


}