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

package com.nimbits.mobile.startup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.nimbits.mobile.HomeActivity;
import com.nimbits.mobile.R;
import com.nimbits.mobile.server.BufferService;
import com.nimbits.mobile.server.ServerActivity;
import com.nimbits.mobile.startup.async.StartupTask;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.http.UrlContainer;

import java.util.ArrayList;
import java.util.List;

public class StartupActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Activity activity;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.splash);
        ImageView myImageView = (ImageView) findViewById(R.id.nimbits_transparent_logo);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        if (myFadeInAnimation != null) {
            myImageView.startAnimation(myFadeInAnimation);
        }
        activity = this;

        try {
            ApplicationInfo ai = getApplication().getPackageManager().getApplicationInfo(getApplication().getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = null;
            if (ai.metaData != null) {
                apiKey = (String) ai.metaData.get(Const.API_KEY_ID);
            }
            Nimbits.setApiKey(apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            finish();
        }
        final SharedPreferences settings = getSharedPreferences(getString(R.string.app_name), 0);
        final String base_url = settings.getString(getString(R.string.base_url_setting), getString(R.string.base_url));
        Nimbits.base = UrlContainer.getInstance(base_url);
        startService(new Intent(this, BufferService.class));
        getLoaderManager().initLoader(0, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            // Potentially filter on ProfileQuery.IS_PRIMARY
            cursor.moveToNext();
        }

        if (emails.isEmpty()) {
            AccountManager am = AccountManager.get(this);
            if (am != null) {
                Account[] accounts = am.getAccountsByType("com.google");
                for (Account account : accounts) {
                    //  if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;
                    emails.add(possibleEmail);
                    //}
                }
            }
        }


        if (emails.isEmpty()) {
            AccountManager am = AccountManager.get(this);
            if (am != null) {
                Account[] accounts = am.getAccounts();
                for (Account account : accounts) {
                    //  if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;
                    emails.add(possibleEmail);
                    //}
                }
            }
        }


        if (!emails.isEmpty()) {
            Nimbits.email = emails.get(0);
            StartupTask.getInstance(new StartupTask.StartupListener() {
                @Override
                public void onLoginSuccess(List<User> response) {
                    Nimbits.session = (response.get(0));
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onLoginFail() {
                    fail();
                }
            }).execute(activity);
        } else {
            fail();
        }


    }

    private void fail() {
        Crashlytics.log("Login Failed");
        CharSequence text = "There was a problem authenticating to Nimbits with the Google Account on this phone. " +
                "You may need to setup your account first, or set your Base URL to an active instance. " +
                "Please visit nimbits.com and login to the public cloud first.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(activity, text, duration);
        toast.show();
        finish();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}