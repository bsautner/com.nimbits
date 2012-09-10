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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.user;


import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.*;
import com.nimbits.client.model.billing.Billing;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;
import java.util.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public interface User extends Entity, Serializable {

    Date getLastLoggedIn();

    void setLastLoggedIn(final Date lastLoggedIn);

    EmailAddress getEmail() throws NimbitsException;

    String getTwitterTokenSecret();

    void setTwitterTokenSecret(final String twitterTokenSecret);

    String getTwitterToken();

    void setTwitterToken(final String twitterToken);

    String getFacebookToken();

    void setFacebookToken(final String facebookToken);

    long getFacebookID();

    void setFacebookID(final long facebookID);

    boolean isRestricted();

    void addAccessKey(AccessKey key);

    List<AccessKey> getAccessKeys();

    boolean isLoggedIn();

    void setLoggedIn(boolean loggedIn);

    String getLoginUrl();

    void setLoginUrl(String loginUrl);

    String getLogoutUrl();

    void setLogoutUrl(String logoutUrl);

    boolean isUserAdmin();

    void setUserAdmin(boolean userAdmin);


    boolean isBillingEnabled();

    void setBillingEnabled(boolean billingEnabled);
}
