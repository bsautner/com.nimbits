/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
