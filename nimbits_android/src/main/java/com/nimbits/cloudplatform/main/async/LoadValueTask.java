package com.nimbits.cloudplatform.main.async;

import android.os.AsyncTask;
import android.util.Log;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/13/13
 * Time: 8:57 AM
 */
public class LoadValueTask extends AsyncTask<Object, List<Value>, List<Value>> {

    public LoadValueTaskListener mListener;

    private LoadValueTask() {

    }


    public interface LoadValueTaskListener {
        public void onSuccess(Value response);

    }

    public static LoadValueTask getInstance(final LoadValueTaskListener listener) {
        LoadValueTask instance = new LoadValueTask();

        instance.setListener(listener);
        return instance;

    }

    public void setListener(final LoadValueTaskListener mListener) {
        this.mListener = mListener;
    }


    @Override
    protected List<Value> doInBackground(final Object... objects) {
        List<Value> response;
        Entity e = (Entity) objects[0];

        Log.v("nimbits", "getting value");

            response = Transaction.getValue(e);

        Log.v("nimbits", "got a value for " + e.getKey());
        return response;


    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(final List<Value> response) {

        super.onPostExecute(response);
        if (!response.isEmpty()) {
            if (mListener != null) {
                mListener.onSuccess(response.get(0));
            }


        }

    }


}
 
  