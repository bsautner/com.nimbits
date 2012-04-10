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


import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.Date;


//import com.google.appengine.api.users.User;


public class UserModel extends EntityModel implements Serializable, User {

   // private String key;

    private Date dateCreated;

    private Date lastLoggedIn;

    private String emailAddress;

    private String facebookToken;

    private String secret;

    private String twitterToken;

    private String twitterTokenSecret;

    private long facebookID;

    private int authLevel = AuthLevel.readWrite.getCode();

    /**
     *
     */
    private static final long serialVersionUID =1L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();
        authLevel= AuthLevel.restricted.getCode();

    }



    public UserModel(final User u) throws NimbitsException {
        super(u);
        this.dateCreated = u.getDateCreated();
        this.lastLoggedIn = u.getLastLoggedIn();
        this.secret = u.getSecret();
        this.authLevel = u.getAuthLevel().getCode();
        this.emailAddress = u.getEmail().getValue();
        this.facebookToken = u.getFacebookToken();
        this.twitterToken = u.getTwitterToken();
        this.twitterTokenSecret = u.getTwitterTokenSecret();
        this.facebookID = u.getFacebookID();
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
    public String getSecret() {
        return secret;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.get(this.authLevel);
    }

    @Override
    public void setAuthLevel(AuthLevel level) {
        this.authLevel =level.getCode();
    }

    @Override
    public boolean isRestricted() {
        return AuthLevel.get(this.authLevel).equals(AuthLevel.restricted);
    }

    @Override
    public void setSecret(final String secret) {
        this.secret = secret;
    }



    @Override
    public EmailAddress getEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
    }


}
