/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/5/11
 * Time: 4:12 PM
 */
public class UserFactory {

//    public static NimbitsUser createNimbitsUser(EmailAddress emailAddress, String secretKey) {
//        return new NimbitsUser(emailAddress, secretKey);
//    }

    public static NimbitsUser createNimbitsUser(final String emailAddress, final String secretKey) throws NimbitsException {
        final EmailAddress emailAddress1 = CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
        return new NimbitsUser(emailAddress1, secretKey);
    }
    public static NimbitsUser createNimbitsUser(final EmailAddress emailAddress, final String secretKey) throws NimbitsException {
          return new NimbitsUser(emailAddress, secretKey);
    }
    public static GoogleUser createGoogleUser(final EmailAddress emailAddress, final String googlePassword) {
        return new GoogleUser(emailAddress, googlePassword);
    }

    public static GoogleUser createGoogleUser(final String email, final String googlePassword) throws NimbitsException {
        final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
        return new GoogleUser(emailAddress, googlePassword);


    }
}
