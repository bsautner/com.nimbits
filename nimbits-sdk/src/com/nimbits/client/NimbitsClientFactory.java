package com.nimbits.client;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.exceptions.GoogleAuthenticationException;
import com.nimbits.user.GoogleUser;
import com.nimbits.user.NimbitsUser;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/13/11
 * Time: 12:41 PM
 */
public class NimbitsClientFactory {
    private static NimbitsClientImpl instance;

    protected NimbitsClientFactory() {
    }


    public static NimbitsClient getInstance(final NimbitsUser n, final String hostUrl) {
        if (instance == null) {
            instance = new NimbitsClientImpl(n, hostUrl);
        } else if (!instance.getHost().equals(hostUrl)) {
            instance = new NimbitsClientImpl(n, hostUrl);
        }
        return instance;

    }

    public static NimbitsClient getInstance(final GoogleUser g, final String hostUrl) throws NimbitsException {
        if (instance == null) {
            instance = new NimbitsClientImpl(g, hostUrl);
        } else if (!instance.getHost().equals(hostUrl)) {
            instance = new NimbitsClientImpl(g, hostUrl);
        }
        return instance;

    }

    /**
     * Used to get a google auth cookie when you already have the token. i.e on an android device.
     *
     * @param authToken
     * @param email
     * @param hostUrl
     * @return
     * @throws Exception
     */
    public static NimbitsClient getInstance(final String authToken, final EmailAddress email, final String hostUrl) throws NimbitsException, GoogleAuthenticationException {
        if (instance == null) {
            instance = new NimbitsClientImpl(authToken, email, hostUrl);
        } else if (!instance.getHost().equals(hostUrl)) {
            instance = new NimbitsClientImpl(authToken, email, hostUrl);
        }

        return instance;

    }
}
