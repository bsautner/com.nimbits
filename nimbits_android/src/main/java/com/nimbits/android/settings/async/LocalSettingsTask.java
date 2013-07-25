package com.nimbits.android.settings.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 9:04 AM
 */
public class LocalSettingsTask extends AsyncTask<Object, SimpleValue<String>, SimpleValue<String>> {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private LocalSettingsTaskListener listener;

    private LocalSettingsTask() {

    }


    public static LocalSettingsTask getInstance() {
        LocalSettingsTask instance = new LocalSettingsTask();

        return instance;

    }

    public interface LocalSettingsTaskListener {
        void onRead(SimpleValue<String> response);
    }

    public static LocalSettingsTask getInstance(LocalSettingsTaskListener listener) {
        LocalSettingsTask instance = new LocalSettingsTask();
        instance.setListener(listener);
        return instance;

    }

    private void setListener(LocalSettingsTaskListener listener) {
        this.listener = listener;
    }


    @Override
    protected SimpleValue<String> doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        Action action = (Action) objects[1];
        Parameters name = (Parameters) objects[2];


        settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
        if (action.equals(Action.create)) {
            SimpleValue<String> value = (SimpleValue<String>) objects[3];


            editor = settings.edit();
            editor.putString(name.getText(), value.toString());
            editor.commit();
            return value;

        } else {
            String sample = settings.getString(name.getText(), "");

            SimpleValue<String> response = SimpleValue.getInstance(sample);
            listener.onRead(response);
            return response;

        }
    }

}

 
  