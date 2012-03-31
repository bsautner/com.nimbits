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

package com.nimbits.client.model;

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.io.Serializable;

public class LoginInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean loggedIn = false;
    private String loginUrl;
    private String logoutUrl;
    private String emailAddress;
    private User user;
    private boolean userAdmin;

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public EmailAddress getEmailAddress() {
        return CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
    }

    public void setEmailAddress(final EmailAddress emailAddress) {
        this.emailAddress = emailAddress.getValue();
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}