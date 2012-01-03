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

package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


//import com.google.appengine.api.users.User;


public class GxtUserModel extends BaseModelData {
    private Long id;


    private Boolean sendEmail;


    private Date dateCreated;


    private List<Long> connections;


    private Long facebookID;


    private EmailAddress email;


    private String facebookToken;


    private String host;


    private String secret;


    private String tag;


    private String twitterToken;

    private String twitterTokenSecret;


    private String userSource;


    private String uuid;

    // A user that can only work with public data

    private boolean restricted;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public GxtUserModel() {
    }

    public GxtUserModel(long id) {
        this.id = id;
    }

    public GxtUserModel(final User u) {
        this.id = u.getId();
        this.dateCreated = u.getDateCreated();
        this.sendEmail = u.getSendEmail();
        this.host = u.getHost();
        this.uuid = u.getUuid();
        this.secret = u.getSecret();
        //Resolves Type 'org.datanucleus.sco.backed.ArrayList' was not included in the set of types which can be serialized by this SerializationPolicy

        List<Long> l = new ArrayList<Long>();
        for (Long x : u.getConnections()) {
            l.add(x);
        }

        this.connections = l;
        this.restricted = u.isRestricted();
        this.email = u.getEmail();
        this.facebookToken = u.getFacebookToken();
        this.userSource = u.getUserSource();
        this.twitterToken = u.getTwitterToken();
        this.twitterTokenSecret = u.getTwitterTokenSecret();
        this.facebookID = u.getFacebookID();
        String shortEmail = email.getValue();
        if (email != null && email.getValue().length() > 8) {
            shortEmail = email.getValue().substring(0, 8) + "...";
        }
        set(Const.PARAM_NAME, shortEmail);
        set(Const.PARAM_PATH, Const.PATH_CONNECTION_IMAGE);
    }


    public GxtUserModel(final EmailAddress email, final String uuid) {
        dateCreated = new Date();
        this.email = email;
        this.sendEmail = true;
        this.uuid = uuid;

    }

    void setEmail(EmailAddress email) {
        this.email = email; // emails are records with all lower
        // case
    }

    void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Long> getConnections() {
        if (connections == null) {
            connections = new LinkedList<Long>();
        }
        return connections;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return new Date(this.dateCreated.getTime());
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = new Date(dateCreated.getTime());
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    public void setTwitterTokenSecret(String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public Long getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(Long facebookID) {
        this.facebookID = facebookID;
    }

    public String getUuid() {
        return uuid;
    }

    void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void addConnection(Long id) {
        if (connections == null) {
            connections = new ArrayList<Long>();
        }
        if (!connections.contains(id)) {
            connections.add(id);
        }
    }

    public void removeConnection(Long id) {
        connections.remove(id);
    }

    public Boolean getSendEmail() {
        return (sendEmail == null) ? false : sendEmail;
    }

    public EmailAddress getEmail() {
        return email;
    }
}
