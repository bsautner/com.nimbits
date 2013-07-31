package com.nimbits.android.main.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 2:12 PM
 */
public class LoadEntityTask<T> extends AsyncTask<Object, List<T>, List<T>> {

    private LoadEntityTask() {

    }

    public static LoadEntityTask getInstance(LoadPointTaskListener listener) {
        LoadEntityTask instance = new LoadEntityTask();
        instance.setListener(listener);
        return instance;
    }


    private LoadPointTaskListener<T> mListener;

    public interface LoadPointTaskListener<T> {
        public void onSuccess(List<T> response);

    }

    private void setListener(LoadPointTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<T> doInBackground(Object... objects) {
        List<T> response;
        Entity entity = (Entity) objects[0];
        Class clz = (Class) objects[1];

            response = (List<T>) Transaction.getEntity(SimpleValue.getInstance(entity.getKey())
                    , entity.getEntityType(), clz);

        return response;


    }

    @Override
    protected void onPostExecute(List<T> ts) {
        super.onPostExecute(ts);

            mListener.onSuccess(ts);

    }

} 
 
  