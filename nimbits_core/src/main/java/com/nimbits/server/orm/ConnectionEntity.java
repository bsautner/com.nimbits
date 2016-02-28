/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import org.apache.commons.lang3.StringUtils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.UUID;


@SuppressWarnings("unused")
@PersistenceCapable
public class ConnectionEntity extends EntityStore implements Connection {


    @Persistent
    private String approvalKey;
    @Persistent
    private boolean approved;
    @Persistent
    private String targetEmail;


    public ConnectionEntity() {
    }

    public ConnectionEntity(final Connection aConnection) {
        super(aConnection);
        this.approvalKey = aConnection.getApprovalKey();
        this.approved = aConnection.isApproved();
        this.targetEmail = aConnection.getTargetEmail();

    }


    @Override
    public void update(final Entity aConnection) {
        super.update(aConnection);
        final Connection update = (Connection) aConnection;
        this.approvalKey = update.getApprovalKey();
        this.approved = update.isApproved();
        this.targetEmail = update.getTargetEmail();
    }


    @Override
    public void validate(User user) {
        super.validate(user);
        if (StringUtils.isEmpty(this.approvalKey)) {
            this.approvalKey = UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString();
        }
    }

    @Override
    public String getApprovalKey() {
        return approvalKey;
    }

    @Override
    public boolean isApproved() {
        return approved;
    }

    @Override
    public String getTargetEmail() {
        return targetEmail;
    }

    @Override
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
