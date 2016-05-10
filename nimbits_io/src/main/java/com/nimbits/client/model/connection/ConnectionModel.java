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

package com.nimbits.client.model.connection;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;


public class ConnectionModel extends EntityModel implements Serializable, Connection {
    @Expose
    private String approvalKey;
    @Expose
    private boolean approved;
    @Expose
    private String targetEmail;


    private ConnectionModel() {

    }


    protected ConnectionModel(String id, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String approvalKey, boolean approved, String targetEmail) {
        super(id, name, description, entityType, protectionLevel, parent, owner);
        this.approvalKey = approvalKey;
        this.approved = approved;
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

    public static class Builder extends EntityBuilder {

        private final EntityType type = EntityType.connection;

        private String approvalKey;

        private boolean approved;

        private String targetEmail;

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.category);
            return this;
        }

        public Connection create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }


            return new ConnectionModel(id, name, description, type, protectionLevel, parent, owner, approvalKey, approved, targetEmail);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }



        private void initEntity(Entity anEntity) {


            this.id = anEntity.getId();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = type;
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();


        }

        public Builder init(Connection e) {
            initEntity(e);
            this.approvalKey = e.getApprovalKey();
            this.approved = e.isApproved();
            this.targetEmail = e.getTargetEmail();
            return this;
        }

        public Builder approvalKey(String approvalKey) {
            this.approvalKey = approvalKey;
            return this;
        }

        public Builder approved(boolean approved) {
            this.approved = approved;
            return this;
        }

        public Builder targetEmail(String targetEmail) {
            this.targetEmail = targetEmail;
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        @Override
        public Builder protectionLevel(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
            return this;
        }
        @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }
        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }
        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }
        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }


        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }
    }
}
