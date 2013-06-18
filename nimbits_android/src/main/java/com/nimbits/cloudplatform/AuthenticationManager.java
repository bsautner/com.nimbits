package com.nimbits.cloudplatform;


import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

    public static SimpleValue<String> getToken(final Context context) throws Exception {


        SimpleValue<String> authToken;

        if (Nimbits.token.isEmpty()) {
            final AccountManager mgr = AccountManager.get(context);
            final Account[] accounts = mgr.getAccountsByType(Parameters.comGoogle.getText());

            if (accounts.length > 0) {
                AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(accounts[0], Const.CONST_AH, null, (Activity) context, null, null);
                try {
                    final Bundle authTokenBundle = accountManagerFuture.getResult();

                    String result = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
                    authToken = SimpleValue.getInstance(result);
                    Nimbits.token = (authToken);
                    mgr.invalidateAuthToken(Parameters.comGoogle.getText(), authToken.getValue());
                    Log.i(Android.N, "got new token: " + authToken);
                } catch (OperationCanceledException e) {
                    throw new  Exception(e);
                } catch (IOException e) {
                    throw new  Exception(e);
                } catch (AuthenticatorException e) {
                    throw new  Exception(e);
                }
            } else {
                throw new  Exception("Could not find a Google Account on this device");

            }
            return Nimbits.token;
        } else {

            Log.i(Android.N, "using cached token! Hazaah!");
            return Nimbits.token;
        }


    }


}

