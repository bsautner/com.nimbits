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

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.accesskey.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;

import javax.jdo.annotations.*;
import java.util.*;


//import com.google.appengine.api.users.User;

@PersistenceCapable
public class UserEntity extends EntityStore implements User {


    @Persistent
    private Date lastLoggedIn;

    @Persistent
    private Long facebookID;

    @Persistent
    private String facebookToken;

    @Persistent
    private String twitterToken;

    @Persistent
    private String twitterTokenSecret;

    // A user that can only work with public data
    @NotPersistent
    private List<AccessKey> accessKeys;
    /**
     *
     */
    private static final long serialVersionUID = 1L;




    @Override
    public Date getLastLoggedIn() {
        return this.lastLoggedIn;// == null ? new Date(this.dateCreated.getTime()) : new Date(this.lastLoggedIn.getTime());
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {
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
    public void setFacebookID(final long facebookID) {
        this.facebookID = facebookID;
    }
    @SuppressWarnings("unused")
    protected UserEntity() {
    }

    public UserEntity(final Entity entity) throws NimbitsException {
        super(entity);
       lastLoggedIn = new Date();
        //  this.key = KeyFactory.createKey(UserEntity.class.getSimpleName(), entity.getKey());

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
    public EmailAddress getEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(key.getName());
    }
    @Override
    public long getFacebookID() {
        return facebookID == null ? 0 : facebookID;
    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        User u = (User)update;
        this.lastLoggedIn = u.getLastLoggedIn();
        this.facebookID = u.getFacebookID();
        this.facebookToken = u.getFacebookToken();
        this.twitterToken = u.getTwitterToken();
        this.twitterTokenSecret = u.getTwitterTokenSecret();
    }

    @Override
    public void validate() throws NimbitsException {
        super.validate();

    }
}
