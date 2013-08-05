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