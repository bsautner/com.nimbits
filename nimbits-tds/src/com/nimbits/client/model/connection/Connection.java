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

package com.nimbits.client.model.connection;

import com.nimbits.client.model.email.EmailAddress;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/17/11
 * Time: 12:53 PM
 */
public interface Connection extends Serializable {

    String getRequestorID();

    void setRequestorID(final String requestorID);

    EmailAddress getTargetEmail();

    void setTargetEmail(final EmailAddress targetEmail);

    Date getRequestDate();

    void setRequestDate(final Date requestDate);

    Date getApprovedDate();

    void setApprovedDate(final Date approvedDate);

    Boolean getApproved();

    void setApproved(final boolean approved);

    String getKey();

    void setRequestorEmail(final EmailAddress requestorEmail);

    EmailAddress getRequestorEmail();

    void setRejected(final boolean rejected);

    Boolean getRejected();
}
