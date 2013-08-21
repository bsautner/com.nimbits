/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android.startup.async;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.crashlytics.android.Crashlytics;
import com.nimbits.android.AuthenticationManager;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.http.HttpHelper;
import com.nimbits.cloudplatform.http.UrlContainer;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
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


        try {
            SimpleValue<String> authToken = AuthenticationManager.getToken(context);
            Nimbits.token =(authToken);
            Nimbits.base =(baseUri);
            List<Cookie> authCookie = HttpHelper.getAuthCookie(gaeAppLoginUri, authToken.toString(), base_url);
            Nimbits.cookie = (authCookie.get(0));
        } catch (AuthenticatorException e) {
            Crashlytics.logException(e);
        } catch (OperationCanceledException e) {
            Crashlytics.logException(e);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return Transaction.getSession();

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
