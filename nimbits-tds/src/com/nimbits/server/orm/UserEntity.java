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

package com.nimbits.server.orm;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;
import java.util.Date;


//import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class UserEntity implements User {
    @PrimaryKey
    @Persistent
    private Key key;

    @Persistent
    private Date dateCreated;

    @Persistent
    private Date lastLoggedIn;

    @Persistent
    private Long facebookID;

    @Persistent
    private String facebookToken;

    @Persistent
    private String secret;

    @Persistent
    private String twitterToken;

    @Persistent
    private String twitterTokenSecret;

    // A user that can only work with public data
    @NotPersistent
    private boolean restricted;
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public Date getLastLoggedIn() {
        return this.lastLoggedIn;// == null ? new Date(this.dateCreated.getTime()) : new Date(this.lastLoggedIn.getTime());
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {
        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }


    @Persistent
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
    public void setFacebookID(final long facebookID) {
        this.facebookID = facebookID;
    }
    @SuppressWarnings("unused")
    protected UserEntity() {
    }

    public UserEntity(final Entity entity, final EmailAddress email) {
        dateCreated = new Date();
        lastLoggedIn = dateCreated;
        this.key = KeyFactory.createKey(UserEntity.class.getSimpleName(), entity.getKey());

    }

    @Override
    public String getSecret() {
        return secret;
    }
    @Override
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    @Override
    public boolean isRestricted() {
        return restricted;
    }
    @Override
    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }
    @Override
    public String getKey() {
        return key.getName();
    }
    @Override
    public EmailAddress getEmail() {
        return CommonFactoryLocator.getInstance().createEmailAddress(key.getName());
    }
    @Override
    public long getFacebookID() {
        return facebookID == null ? 0 : facebookID;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


//    public long getID() {
//        return id == null ? 0 : id;
//    }
}
