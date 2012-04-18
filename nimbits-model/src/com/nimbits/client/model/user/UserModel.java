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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.user;


import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//import com.google.appengine.api.users.User;


public class UserModel extends EntityModel implements Serializable, User {


    private Date dateCreated;

    private Date lastLoggedIn;

    private String emailAddress;

    private String facebookToken;

    private String twitterToken;

    private String twitterTokenSecret;

    private long facebookID;


    private List<AccessKey> accessKeys;

    /**
     *
     */
    private static final long serialVersionUID =1L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();


    }

    public UserModel(final User u) throws NimbitsException {
        super(u);
        if (u != null) {
            this.dateCreated = u.getDateCreated();
            this.lastLoggedIn = u.getLastLoggedIn();
            this.accessKeys = u.getAccessKeys();
            this.emailAddress = u.getEmail().getValue();
            this.facebookToken = u.getFacebookToken();
            this.twitterToken = u.getTwitterToken();
            this.twitterTokenSecret = u.getTwitterTokenSecret();
            this.facebookID = u.getFacebookID();
        }
        else {

        }
    }

    public UserModel(final Entity entity) throws NimbitsException {
        super(entity);
        this.dateCreated = new Date();
        this.lastLoggedIn =  new Date();
        this.emailAddress = entity.getName().getValue();

    }

    @Override
    public Date getDateCreated() {
        return new Date(this.dateCreated.getTime());
    }

    @Override
    public Date getLastLoggedIn() {
        return this.lastLoggedIn;
    }

    @Override
    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }


    @Override
    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    @Override
    public void setTwitterTokenSecret(final String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    @Override
    public String getTwitterToken() {
        return twitterToken;
    }

    @Override
    public void setTwitterToken(final String twitterToken) {
        this.twitterToken = twitterToken;
    }

    @Override
    public String getFacebookToken() {
        return facebookToken;
    }

    @Override
    public void setFacebookToken(final String facebookToken) {
        this.facebookToken = facebookToken;
    }

    @Override
    public long getFacebookID() {
        return facebookID;
    }

    @Override
    public void setFacebookID(long facebookID) {
        this.facebookID = facebookID;
    }

    @Override
    public boolean isRestricted() {
        if (accessKeys == null) {
            return true;
        }
        for (AccessKey key : accessKeys) {
            if (key.getAuthLevel().compareTo(AuthLevel.restricted) > 0)
            {
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
    public void addAccessKey(AccessKey key) {
        if (accessKeys == null) {
            accessKeys = new ArrayList<AccessKey>(1);
        }
        accessKeys.add(key);
    }

    @Override
    public void addAccessKeys(List<AccessKey> key) {
        if (accessKeys == null) {
            accessKeys = new ArrayList<AccessKey>(key.size());
        }
        accessKeys.addAll(key);
    }


    @Override
    public EmailAddress getEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
    }


}
