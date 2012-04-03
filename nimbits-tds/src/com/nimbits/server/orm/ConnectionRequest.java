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
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;

import javax.jdo.annotations.*;
import java.util.Date;


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ConnectionRequest implements Connection {
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


    public ConnectionRequest() {
    }

    public ConnectionRequest(final Connection c) {
        this.requestorID = c.getRequestorID();
        this.targetEmail = c.getTargetEmail().getValue();
        this.requestorEmail = c.getRequestorEmail().getValue();
        this.requestDate = c.getRequestDate();
        this.approved = c.getApproved();
        this.rejected = c.getRejected();
     }

    public ConnectionRequest(final String requestorID, final EmailAddress requestorEmail, final EmailAddress targetEmail, final String uuid) {
        this.requestorID = requestorID;
        this.targetEmail = targetEmail.getValue();
        this.requestorEmail = requestorEmail.getValue();
        this.requestDate = new Date();
        this.approved = false;
        this.rejected = false;

    }

    public String getRequestorID() {
        return requestorID;
    }

    public void setRequestorID(final String requestorId) {
        this.requestorID = requestorId;
    }

    public EmailAddress getTargetEmail() {
        return CommonFactoryLocator.getInstance().createEmailAddress(targetEmail);
    }

    public void setTargetEmail(final EmailAddress targetEmail) {
        this.targetEmail = targetEmail.getValue();
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(final Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(final Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public String getKey() {
        return key.getName();
    }

    public EmailAddress getRequestorEmail() {
        return CommonFactoryLocator.getInstance().createEmailAddress(requestorEmail);
    }

    public void setRequestorEmail(final EmailAddress requestorEmail) {
        this.requestorEmail = requestorEmail.getValue();
    }

    public Boolean getRejected() {
        if (rejected == null) {
            rejected = false;
        }
        return rejected;
    }

    public void setRejected(final boolean rejected) {
        this.rejected = rejected;
    }


}
