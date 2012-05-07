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
import com.google.appengine.api.datastore.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;

import javax.jdo.annotations.*;
import java.util.*;


@PersistenceCapable
public class ConnectionRequestEntity implements ConnectionRequest {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private Boolean approved;

    @Persistent
    private Boolean rejected;

    @Persistent
    private Date approvedDate;

    @Persistent
    private Date requestDate;

    @Persistent
    private String requestorID;

    @Persistent
    private String requestorEmail;

    @Persistent
    private String targetEmail;


    protected  ConnectionRequestEntity() {
    }

    public ConnectionRequestEntity(final ConnectionRequest c) throws NimbitsException {
        this.requestorID = c.getRequestorID();
        this.targetEmail = c.getTargetEmail().getValue();
        this.requestorEmail = c.getRequestorEmail().getValue();
        this.requestDate = c.getRequestDate();
        this.approved = c.getApproved();
        this.rejected = c.getRejected();
        if ( c.getKey() != null) {
            this.key = KeyFactory.createKey(ConnectionRequestEntity.class.getSimpleName(), c.getKey());
        }
    }

    public ConnectionRequestEntity(final String requestorID,
                                   final CommonIdentifier requestorEmail,
                                   final CommonIdentifier targetEmail,
                                   final String uuid) {
        this.requestorID = requestorID;
        this.targetEmail = targetEmail.getValue();
        this.requestorEmail = requestorEmail.getValue();
        this.requestDate = new Date();
        this.approved = false;
        this.rejected = false;

    }

    @Override
    public String getRequestorID() {
        return requestorID;
    }

    @Override
    public void setRequestorID(final String requestorId) {
        this.requestorID = requestorId;
    }

    @Override
    public EmailAddress getTargetEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(targetEmail);
    }

    @Override
    public void setTargetEmail(final EmailAddress targetEmail) {
        this.targetEmail = targetEmail.getValue();
    }

    @Override
    public Date getRequestDate() {
        return requestDate;
    }

    @Override
    public void setRequestDate(final Date requestDate) {
        this.requestDate = new Date(requestDate.getTime());
    }

    @Override
    public Date getApprovedDate() {
        return approvedDate;
    }

    @Override
    public void setApprovedDate(final Date approvedDate) {
        this.approvedDate = new Date(approvedDate.getTime());
    }

    @Override
    public Boolean getApproved() {
        return approved;
    }

    @Override
    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    @Override
    public Long getKey() {
        return key.getId();
    }

    @Override
    public EmailAddress getRequestorEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(requestorEmail);
    }

    @Override
    public void setRequestorEmail(final EmailAddress requestorEmail) {
        this.requestorEmail = requestorEmail.getValue();
    }

    @Override
    public Boolean getRejected() {
        if (rejected == null) {
            rejected = false;
        }
        return rejected;
    }

    @Override
    public void setRejected(final boolean rejected) {
        this.rejected = rejected;
    }


}
