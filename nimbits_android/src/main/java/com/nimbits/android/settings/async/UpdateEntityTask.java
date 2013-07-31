package com.nimbits.android.settings.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 12:36 PM
 */
public class UpdateEntityTask<T> extends AsyncTask<Object, List<T>, List<T>> {

    private UpdateEntityTask() {

    }

    public static UpdateEntityTask getInstance(UpdateEntityTaskListener listener) {
        final UpdateEntityTask instance = new UpdateEntityTask();
        instance.setListener(listener);
        return instance;


    }


    private UpdateEntityTaskListener<T> mListener;

    public interface UpdateEntityTaskListener<T> {
        public void onSuccess(List<T> response);

        @SuppressWarnings("unused")
        public void onFail(Exception ex);

    }

    public void setListener(UpdateEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<T> doInBackground(Object... objects) {
        List<T> response;
        Entity e = (Entity) objects[0];
        Class clz = (Class) objects[1];

            response = (List<T>) Transaction.updateEntity(e, clz);

        return response;


    }

    @Override
    protected void onPostExecute(List<T> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  