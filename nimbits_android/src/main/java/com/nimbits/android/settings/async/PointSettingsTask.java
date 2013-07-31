package com.nimbits.android.settings.async;

import android.os.AsyncTask;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.helper.PointHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 11:59 AM
 */
public class PointSettingsTask extends AsyncTask<Object, List<Point>, List<Point>> {
    private static PointSettingsTaskListener mListener;

    private PointSettingsTask() {

    }

    public static PointSettingsTask getInstance(PointSettingsTaskListener listener) {
        final PointSettingsTask instance = new PointSettingsTask();
        instance.setListener(listener);
        return instance;


    }


    public interface PointSettingsTaskListener {
        public void onSuccess(List<Point> response);


    }

    private void setListener(PointSettingsTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Point> doInBackground(Object... objects) {

        Entity e = (Entity) objects[0];
        try {
            return  Arrays.asList( PointHelper.getPoint(e.getName().getValue()));

        } catch (Exception e1) {
            return Collections.emptyList();


        }
    }

    @Override
    protected void onPostExecute(List<Point> ts) {
        super.onPostExecute(ts);
        mListener.onSuccess(ts);
    }

} 
 
  