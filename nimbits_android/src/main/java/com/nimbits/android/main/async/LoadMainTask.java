package com.nimbits.android.main.async;

import android.os.AsyncTask;
import android.util.Log;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.http.HttpHelper;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 8:26 AM
 */
public class LoadMainTask extends AsyncTask<Object, Integer, List<Entity>> {


    private LoadListener mListener;

    public interface LoadListener {
        public void onSuccess(List<Entity> response);

        public void onProgress(int progress);

    }

    public void setListener(LoadListener listener) {
        mListener = listener;
    }

    public LoadMainTask() {

    }


    @Override
    protected List<Entity>doInBackground(Object... objects) {

        publishProgress(10);

        List<Entity>response =  Transaction.getTree();

        Nimbits.tree = response;

        List<Entity> retObj = new ArrayList<Entity>(response.size());
        for (Entity e : response) {
            if (e.getEntityType().isAndroidReady()) {
                retObj.add(e);
            }
        }

        publishProgress(100);
        return retObj;

    }

    @Override
    protected void onPostExecute(List<Entity> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mListener.onProgress(progress[0]);
        Log.v("nimbits", "progress update");
    }
}