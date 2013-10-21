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
import com.nimbits.Nimbits;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.transaction.Transaction;
import com.nimbits.transaction.TransactionFactory;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 8:26 AM
 */
public class LoadControlTask extends AsyncTask<Object, Integer, AndroidControl> {
    private Transaction transactions = TransactionFactory.getInstance(SessionSingleton.getInstance().getServer(), SessionSingleton.getInstance().getEmail());

    public LoadControlTask() {

    }

    @Override
    protected AndroidControl doInBackground(Object... objects) {

        List<AndroidControl> control = transactions.getControl();
        if (control.isEmpty()) {
            Nimbits.setControl(AndroidControlFactory.getConservativeInstance()); //load highly conservative values since something is wrong.
        } else {
            Nimbits.setControl(control.get(0));
        }
        return Nimbits.getControl();

    }


}