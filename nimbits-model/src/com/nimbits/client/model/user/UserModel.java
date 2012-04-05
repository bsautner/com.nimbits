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


import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;

import java.io.Serializable;
import java.util.Date;


//import com.google.appengine.api.users.User;


public class UserModel implements Serializable, User {

    private String key;

    private Date dateCreated;

    private Date lastLoggedIn;

    private String emailAddress;

    private String facebookToken;

    private String secret;

    private String twitterToken;

    private String twitterTokenSecret;

    private boolean restricted;

    private long facebookID;


    /**
     *
     */
    private static final long serialVersionUID =1L;

    public UserModel() {
    }



    public UserModel(final User u) throws NimbitsException {
        this.key = u.getKey();
        this.dateCreated = u.getDateCreated();
        this.lastLoggedIn = u.getLastLoggedIn();
        this.secret = u.getSecret();
        this.restricted = u.isRestricted();
        this.emailAddress = u.getEmail().getValue();
        this.facebookToken = u.getFacebookToken();
        this.twitterToken = u.getTwitterToken();
        this.twitterTokenSecret = u.getTwitterTokenSecret();
        this.facebookID = u.getFacebookID();
    }


    @Override
    public String getKey() {
        return this.key;
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
    public EmailAddress getEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
    }


}
