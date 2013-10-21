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

package com.nimbits.server.orm;

import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//import com.google.appengine.api.users.User;

@PersistenceCapable
public class UserEntity extends EntityStore implements User {


    @Persistent
    private Date lastLoggedIn;

    @NotPersistent
    private boolean loggedIn = false;

    @NotPersistent
    private String loginUrl;

    @NotPersistent
    private String logoutUrl;

    @NotPersistent
    private boolean userAdmin;

    @NotPersistent
    private String action;

    // A user that can only work with public data
    @NotPersistent
    private List<AccessKey> accessKeys;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Date getLastLoggedIn() {
        return (Date) this.lastLoggedIn.clone();
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {
        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }



    @SuppressWarnings("unused")
    protected UserEntity() {
    }

    public UserEntity(final Entity entity) {
        super(entity);
        lastLoggedIn = new Date();
        this.key = entity.getOwner();

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
    public boolean isRestricted() {
        if (accessKeys == null) {
            return true;
        }
        for (final AccessKey key : accessKeys) {
            if (key.getAuthLevel().compareTo(AuthLevel.restricted) > 0) {
                return false;
            }
        }
        return true;

    }

    @Override
    public EmailAddress getEmail() {
        return CommonFactory.createEmailAddress(getOwner());
    }



    @Override
    public void update(final Entity update) {
        super.update(update);
        final User u = (User) update;
        this.lastLoggedIn = u.getLastLoggedIn();

    }

    @Override
    public void validate(User user)   {
        super.validate(user);

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
    public void setSessionId(String sessionId) {

    }

    @Override
    public String getSessionId() {
        return null;
    }


}
