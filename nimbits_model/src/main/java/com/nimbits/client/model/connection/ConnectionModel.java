/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.connection;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;


public class ConnectionModel extends EntityModel implements Serializable, Connection {

    private String approvalKey;
    private boolean approved;
    private String targetEmail;


    protected ConnectionModel() {

    }

    public ConnectionModel(final Connection c)  {
        super(c);
        this.approvalKey = c.getApprovalKey();
        this.approved = c.isApproved();
        this.targetEmail = c.getTargetEmail();
    }

    public ConnectionModel(Entity en, String targetEmail) {
        super(en);
        this.targetEmail = targetEmail;

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
