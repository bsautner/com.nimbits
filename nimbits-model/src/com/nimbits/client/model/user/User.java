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

import com.nimbits.client.model.email.*;

import java.io.*;
import java.util.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public interface User extends Serializable {

    List<Long> getConnections();

    void addConnection(final long id);

    void removeConnection(final long id);

    boolean getSendEmail();

    void setSendEmail(final boolean sendEmail);

    long getId();

    void setId(final long id);

    Date getDateCreated();

    Date getLastLoggedIn();

    void setLastLoggedIn(final Date lastLoggedIn);

    void setDateCreated(final Date dateCreated);

    EmailAddress getEmail();

    void setEmail(final EmailAddress email);

    String getUserSource();

    void setUserSource(final String userSource);

    String getTwitterTokenSecret();

    void setTwitterTokenSecret(final String twitterTokenSecret);

    String getTwitterToken();

    void setTwitterToken(final String twitterToken);

    String getFacebookToken();

    void setFacebookToken(final String facebookToken);

    long getFacebookID();

    void setFacebookID(final long facebookID);

    void setUuid(final String uuid);

    String getUuid();

    void setHost(final String host);

    String getHost();

    void setSecret(final String secret);

    String getSecret();

    void setRestricted(final boolean restricted);

    boolean isRestricted();

}
