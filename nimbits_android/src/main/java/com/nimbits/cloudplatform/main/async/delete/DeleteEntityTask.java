package com.nimbits.cloudplatform.main.async.delete;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.transaction.Transaction;

/**
 * Author: Benjamin Sautner
 * Date: 1/15/13
 * Time: 11:31 AM
 */
public class DeleteEntityTask extends AsyncTask<Object, Void, Void> {

    public static DeleteEntityTaskListener mListener;

    private DeleteEntityTask() {

    }

    public static DeleteEntityTask getInstance(DeleteEntityTaskListener listener) {
        DeleteEntityTask instance = new DeleteEntityTask();
        instance.setListener(listener);
        return instance;

    }

    public interface DeleteEntityTaskListener {
        public void onSuccess();

    }


    private void setListener(DeleteEntityTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Entity e = (Entity) objects[0];

            Transaction.deleteEntity(e);


        return null;

    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Void response) {
        super.onPostExecute(response);
        mListener.onSuccess();
    }

} 
 
  