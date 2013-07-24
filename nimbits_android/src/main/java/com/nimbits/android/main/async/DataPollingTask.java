package com.nimbits.android.main.async;


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
 
  