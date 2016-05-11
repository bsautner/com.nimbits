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

package com.nimbits.client.model.socket;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;

public class SocketModel extends EntityModel implements Serializable, Socket {
    @Expose
    private String targetApiKey;
    @Expose
    private String targetUrl;
    @Expose
    private String targetPath;
    @Expose
    private String extraParams;


    protected SocketModel(String id, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String targetApiKey, String targetUrl, String targetPath, String extraParams) {
        super(id, name, description, entityType, protectionLevel, parent, owner);
        this.targetApiKey = targetApiKey;
        this.targetUrl = targetUrl;
        this.targetPath = targetPath;
        this.extraParams = extraParams;
    }


    private SocketModel() {
    }

    @Override
    public String getTargetApiKey() {
        return targetApiKey;
    }

    @Override
    public String getTargetUrl() {
        return targetUrl;
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public String getExtraParams() {
        return extraParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SocketModel that = (SocketModel) o;

        if (extraParams != null ? !extraParams.equals(that.extraParams) : that.extraParams != null) return false;
        if (!targetApiKey.equals(that.targetApiKey)) return false;
        if (targetPath != null ? !targetPath.equals(that.targetPath) : that.targetPath != null) return false;
        if (!targetUrl.equals(that.targetUrl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + targetApiKey.hashCode();
        result = 31 * result + targetUrl.hashCode();
        result = 31 * result + (targetPath != null ? targetPath.hashCode() : 0);
        result = 31 * result + (extraParams != null ? extraParams.hashCode() : 0);
        return result;
    }

    public static class Builder extends EntityBuilder {
        private final EntityType type = EntityType.socket;

        private String targetApiKey;

        private String targetUrl;

        private String targetPath;

        private String extraParams;


        public Builder targetApiKey(String targetApiKey) {
            this.targetApiKey = targetApiKey;
            return this;
        }

        public Builder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        public Builder targetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        public Builder extraParams(String extraParams) {
            this.extraParams = extraParams;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Socket create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }


            return new SocketModel(id, name, description, type, protectionLevel, parent, owner,
                    targetApiKey, targetUrl, targetPath, extraParams);
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

        public Builder init(Socket e) {
            initEntity(e);
            targetApiKey = e.getTargetApiKey();
            targetUrl = e.getTargetUrl();
            targetPath = e.getTargetPath();
            extraParams = e.getExtraParams();

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
