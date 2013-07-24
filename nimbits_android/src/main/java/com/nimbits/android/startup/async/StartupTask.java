package com.nimbits.android.startup.async;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.nimbits.android.AuthenticationManager;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.http.HttpHelper;
import com.nimbits.cloudplatform.http.UrlContainer;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.http.cookie.Cookie;

import java.util.Collections;
import java.util.List;


public class StartupTask extends AsyncTask<Object, User, List<User>> {

    private StartupListener mListener;
    private Context context;
    public interface StartupListener {
        public void onLoginSuccess(List<User> response);

        public void onLoginFail();

    }

    private void setListener(StartupListener listener) {
        mListener = listener;
    }
    public static StartupTask getInstance(StartupListener listener) {
    StartupTask task = new StartupTask();
      task.setListener(listener);
        return task;
    }

    @Override
    protected List<User> doInBackground(Object... o) {
        context = (Context) o[0];
        final SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
        final String base_url = settings.getString(context.getString(R.string.base_url_setting), context.getString(R.string.base_url));
        final UrlContainer baseUri = UrlContainer.getInstance(base_url);
        final UrlContainer gaeAppLoginUri = UrlContainer.combine(UrlContainer.getInstance(context.getString(R.string.base_url)), UrlContainer.getInstance(Path.PATH_AH_LOGIN));
        final SimpleValue<String> authToken;
        try {
            setMemCache();

            authToken = AuthenticationManager.getToken(context);
            Nimbits.cacheDir = (context.getCacheDir());
            Nimbits.token =(authToken);
            Nimbits.base =(baseUri);
            List<Cookie> authCookie = HttpHelper.getAuthCookie(gaeAppLoginUri, authToken.toString(), base_url);
            Nimbits.cookie = (authCookie.get(0));
            Nimbits.isExternalStorageAvailable = (isExternalStorageAvailable());
            return Transaction.getSession();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
            return Collections.emptyList();

        }
    }
    private void setMemCache() {
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
        Nimbits.availableMemory = (cacheSize);
    }
    private static boolean isExternalStorageAvailable() {

        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    @Override
    protected void onPostExecute(List<User> response) {
        super.onPostExecute(response);

        if (response.isEmpty()) {
            mListener.onLoginFail();
        } else {
            mListener.onLoginSuccess(response);
        }

    }

}
