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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import com.nimbits.client.model.user.User;
import com.nimbits.mobile.HomeActivity;
import com.nimbits.mobile.R;
import com.nimbits.mobile.application.NimbitsApplication;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.startup.async.StartupTask;
import com.nimbits.mobile.ui.instance.InstanceManager;

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
        NimbitsApplication app = (NimbitsApplication) getApplication();
        try {
            ApplicationInfo ai = app.getPackageManager().getApplicationInfo(app.getPackageName(), PackageManager.GET_META_DATA);
            SessionSingleton.getInstance().setAppInfo(ai);

        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            finish();
        }

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
            SessionSingleton.getInstance().setEmail(emails.get(0));
            StartupTask.getInstance(new StartupTask.StartupListener() {
                @Override
                public void onLoginSuccess(List<User> response) {
                    SessionSingleton.getInstance().setSession(response.get(0));
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
        Intent intent = new Intent(getApplicationContext(), InstanceManager.class);
        Bundle b = new Bundle();

        intent.putExtras(b);
        startActivity(intent);
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