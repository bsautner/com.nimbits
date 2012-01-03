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


import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


//import com.google.appengine.api.users.User;


public class UserModel implements Serializable, User {

    private long id;

    private Date dateCreated;

    private Date lastLoggedIn;

    private List<Long> connections;

    private String emailAddress;

    private String facebookToken;

    private String host;

    private String secret;

    private String twitterToken;

    private String twitterTokenSecret;

    private String userSource;

    private String uuid;

    private boolean restricted;

    private boolean sendEmail;

    private long facebookID;


    /**
     *
     */
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;

    public UserModel() {
    }

    public UserModel(final long id) {
        this.id = id;
    }

    public UserModel(final User u) {
        this.id = u.getId();
        this.dateCreated = u.getDateCreated();
        this.lastLoggedIn = u.getLastLoggedIn();
        this.sendEmail = u.getSendEmail();
        this.host = u.getHost();
        this.uuid = u.getUuid();
        this.secret = u.getSecret();

        //Resolves Type 'org.datanucleus.sco.backed.ArrayList' was not included in the set of types which can be serialized by this SerializationPolicy
        final List<Long> l = new ArrayList<Long>();
        for (final long x : u.getConnections()) {
            l.add(x);
        }


        this.connections = l;
        this.restricted = u.isRestricted();
        this.emailAddress = u.getEmail().getValue();
        this.facebookToken = u.getFacebookToken();
        this.userSource = u.getUserSource();
        this.twitterToken = u.getTwitterToken();
        this.twitterTokenSecret = u.getTwitterTokenSecret();
        this.facebookID = u.getFacebookID();
    }

//    public UserModel(final EmailAddress email, final String uuid) {
//        this.dateCreated = new Date();
//        this.email = email.getValue();
//        this.sendEmail = true;
//        this.uuid = uuid;
//
//    }

    @Override
    public void setEmail(final EmailAddress email) {
        this.emailAddress = email.getValue();
        // case
    }

    @Override
    public List<Long> getConnections() {
        if (connections == null) {
            connections = new LinkedList<Long>();
        }
        return connections;
    }

    @Override
    public boolean getSendEmail() {
        return sendEmail;
    }

    @Override
    public void setSendEmail(final boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(final long id) {
        this.id = id;
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
    public void setDateCreated(final Date dateCreated) {
        this.dateCreated = new Date(dateCreated.getTime());
    }

    @Override
    public String getUserSource() {
        return userSource;
    }

    @Override
    public void setUserSource(final String userSource) {
        this.userSource = userSource;
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
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(final String host) {
        this.host = host;
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
    public EmailAddress getEmail() {
        return CommonFactoryLocator.getInstance().createEmailAddress(emailAddress);
    }

    @Override
    public void addConnection(final long id) {
        if (connections == null) {
            connections = new ArrayList<Long>();
        }
        if (!connections.contains(id)) {
            connections.add(id);
        }
    }

    @Override
    public void removeConnection(final long id) {
        connections.remove(id);
    }
}
