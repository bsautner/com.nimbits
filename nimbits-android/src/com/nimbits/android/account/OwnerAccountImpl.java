package com.nimbits.android.account;

import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.nimbits.client.NimbitsClient;
import com.nimbits.client.NimbitsClientFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;

import java.io.IOException;


public class OwnerAccountImpl implements OwnerAccount {
    private static String tokenStore = null;
    private static EmailAddress emailStore = null;


    @Override
    public EmailAddress getEmail(final Context context) {
        if (emailStore == null) {
            final AccountManager accountManager = AccountManager.get(context);
            final Account account = getAccount(accountManager);

            if (account == null) {
                return null;
            } else {
                emailStore = CommonFactoryLocator.getInstance().createEmailAddress(account.name);
                return emailStore;
            }
        } else {
            return emailStore;

        }

    }

    public void invalidateToken(final Context context, String token) {
        final AccountManager mgr = AccountManager.get(context);
        mgr.invalidateAuthToken(Const.PARAM_GOOGLE_COM, token);
    }

    @Override
    public String getToken(final Context context) {
        String authToken;

        if (tokenStore == null) {
            final AccountManager mgr = AccountManager.get(context);
            final Account[] accounts = mgr.getAccountsByType(Const.PARAM_GOOGLE_COM);

            if (accounts.length > 0) {
                AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(accounts[0], Const.CONST_AH, null, (Activity) context, null, null);
                try {
                    final Bundle authTokenBundle = accountManagerFuture.getResult();

                    authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
                    tokenStore = authToken;
                    Log.i(Const.N, "got new token: " + authToken);
                } catch (OperationCanceledException e) {
                    Log.e(Const.N, e.getMessage(), e);
                    authToken = Const.MESSAGE_NO_ACCOUNT;
                    tokenStore = null;
                } catch (IOException e) {
                    authToken = Const.MESSAGE_NO_ACCOUNT;
                    tokenStore = null;
                    Log.e(Const.N, e.getMessage(), e);
                } catch (AuthenticatorException e) {
                    authToken = Const.MESSAGE_NO_ACCOUNT;
                    tokenStore = null;
                    Log.e(Const.N, e.getMessage(), e);
                }
            } else {
                authToken = Const.MESSAGE_NO_ACCOUNT;
                tokenStore = null;

            }


        } else {
            authToken = tokenStore;
            Log.i(Const.N, "using cached token: " + authToken);
        }
        return authToken;


    }

    @Override
    public NimbitsClient getNimbitsClient(final Context context, final String baseUrl) throws NimbitsException {
        //
        try {
            if (baseUrl.equals(Const.PATH_LOCAL)) {
                EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(Const.TEST_ACCOUNT);
                return NimbitsClientFactory.getInstance(null, emailAddress, baseUrl);
            } else {
                final String authToken = getToken(context);
                final EmailAddress email = getEmail(context);
                return NimbitsClientFactory.getInstance(authToken, email, baseUrl);
            }
        } catch (Exception e) {
            throw new NimbitsException(e.getMessage());
        }
    }

    private Account getAccount(final AccountManager accountManager) {
        final Account[] accounts = accountManager.getAccountsByType(Const.PARAM_GOOGLE_COM);
        return accounts.length > 0 ? accounts[0] : null;


    }


}


