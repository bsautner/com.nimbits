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

package com.nimbits.client.model.user;


import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserModel extends EntityModel implements Serializable, User {

    private Date lastLoggedIn;

    private String emailAddress;

    private List<AccessKey> accessKeys;

    private boolean loggedIn = false;

    private String loginUrl;

    private String logoutUrl;

    private boolean userAdmin;

    private String sessionId;
    /**
     *
     */
    private static final long serialVersionUID = 3L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();
    }

    public UserModel(final User u) {
        super(u);
        if (u != null) {
            this.lastLoggedIn = u.getLastLoggedIn();
            this.accessKeys = u.getAccessKeys();
            this.emailAddress = u.getEmail().getValue();

        }
    }

    public UserModel(final Entity entity) {
        super(entity);
        this.lastLoggedIn = new Date();
        this.emailAddress = entity.getName().getValue();

    }


    @Override
    public Date getLastLoggedIn() {
        return   new Date(lastLoggedIn.getTime());
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {

        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }




    @Override
    public boolean isRestricted() {
        if (accessKeys == null) {
            return true;
        }
        for (final AccessKey key : accessKeys) {
            if (key.getAuthLevel().getCode() > (AuthLevel.restricted.getCode())) {
                return false;
            }
        }
        return true;

    }

    @Override
    public List<AccessKey> getAccessKeys() {
        return this.accessKeys == null ? new ArrayList<AccessKey>(1) : this.accessKeys;
    }

    @Override
    public void addAccessKey(final AccessKey key) {
        if (accessKeys == null) {
            accessKeys = new ArrayList<AccessKey>(1);
        }
        accessKeys.add(key);
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public String getLoginUrl() {
        return loginUrl;
    }

    @Override
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }

    @Override
    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Override
    public boolean isUserAdmin() {
        return userAdmin;
    }

    @Override
    public void setUserAdmin(final boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    @Override
    public EmailAddress getEmail() {
        return CommonFactory.createEmailAddress(emailAddress);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserModel userModel = (UserModel) o;

        if (loggedIn != userModel.loggedIn) return false;
        if (userAdmin != userModel.userAdmin) return false;
        if (accessKeys != null ? !accessKeys.equals(userModel.accessKeys) : userModel.accessKeys != null) return false;
        if (emailAddress != null ? !emailAddress.equals(userModel.emailAddress) : userModel.emailAddress != null)
            return false;
        if (lastLoggedIn != null ? !lastLoggedIn.equals(userModel.lastLoggedIn) : userModel.lastLoggedIn != null)
            return false;
        if (loginUrl != null ? !loginUrl.equals(userModel.loginUrl) : userModel.loginUrl != null) return false;
        if (logoutUrl != null ? !logoutUrl.equals(userModel.logoutUrl) : userModel.logoutUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lastLoggedIn != null ? lastLoggedIn.hashCode() : 0);
        result = 31 * result + emailAddress.hashCode();
        result = 31 * result + (accessKeys != null ? accessKeys.hashCode() : 0);
        result = 31 * result + (loggedIn ? 1 : 0);
        result = 31 * result + (loginUrl != null ? loginUrl.hashCode() : 0);
        result = 31 * result + (logoutUrl != null ? logoutUrl.hashCode() : 0);
        result = 31 * result + (userAdmin ? 1 : 0);

        return result;
    }
}
