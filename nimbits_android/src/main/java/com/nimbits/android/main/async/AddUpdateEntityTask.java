package com.nimbits.android.main.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 12:58 PM
 */
public class AddUpdateEntityTask extends AsyncTask<Object, List<Entity>, List<Entity>> {

    private AddUpdateEntityTask() {

    }

    private static AddUpdateEntityTaskListener mListener;

    public static AddUpdateEntityTask getInstance(AddUpdateEntityTaskListener listener) {
        AddUpdateEntityTask instance = new AddUpdateEntityTask();
        instance.setListener(listener);
        return instance;

    }


    public interface AddUpdateEntityTaskListener<T> {
        public void onSuccess(List<T> response);

        @SuppressWarnings("unused")
        public void onFail( Exception ex);

    }

    public void setListener(AddUpdateEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Entity> doInBackground(Object... objects) {
         Entity e = (Entity) objects[0];
        Class clz = (Class) objects[1];
        boolean isNew = (Boolean) (objects[2]);

            if (isNew) {

                return Transaction.addEntity(e, clz);

            } else {
                return Transaction.updateEntity(e, clz);
            }




    }

    @Override
    protected void onPostExecute(List<Entity> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  