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

package com.nimbits.android;


import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.constants.Android;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;

import java.io.IOException;


public class AuthenticationManager {
    public static void resetToken(final Context context, SimpleValue<String> token) {


        final AccountManager mgr = AccountManager.get(context);
        final Account[] accounts = mgr.getAccountsByType(Parameters.comGoogle.getText());

        mgr.invalidateAuthToken(Parameters.comGoogle.getText(), token.getValue());





    }

    public static SimpleValue<String> getToken(final Context context) throws AuthenticatorException, OperationCanceledException, IOException {


        SimpleValue<String> authToken;

       // if (Nimbits.token.isEmpty()) {
            final AccountManager mgr = AccountManager.get(context);
            final Account[] accounts;
            if (mgr != null) {
                accounts = mgr.getAccountsByType(Parameters.comGoogle.getText());


                if (accounts.length > 0) {
                    AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(accounts[0], Const.CONST_AH, null, (Activity) context, null, null);

                        final Bundle authTokenBundle = accountManagerFuture.getResult();

                        String result = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
                        authToken = SimpleValue.getInstance(result);
                        Nimbits.token = (authToken);
                        mgr.invalidateAuthToken(Parameters.comGoogle.getText(), authToken.getValue());
                        Log.i(Android.N, "got new token: " + authToken);

                }
            }
            return Nimbits.token;
//        } else {
//
//            Log.i(Android.N, "using cached token! Hazaah!");
//            return Nimbits.token;
//        }


    }


}

