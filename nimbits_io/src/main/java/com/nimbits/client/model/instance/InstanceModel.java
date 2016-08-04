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

package com.nimbits.client.model.instance;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.server.Protocol;

import java.io.Serializable;


public class InstanceModel extends EntityModel implements Serializable, Instance {

    @Expose
    private long serverId;
    @Expose
    private String baseUrl;
    @Expose
    private String adminEmail;
    @Expose
    private String version;
    @Expose
    private String password;
    @Expose
    private boolean isDefault;
    @Expose
    private String protocol;
    @Expose
    private boolean socketsEnabled;


    protected InstanceModel(String id, CommonIdentifier name, String description, EntityType entityType, String parent, String owner, long serverId, String baseUrl, String adminEmail, String version, String password, boolean isDefault, String protocol, boolean socketsEnabled) {
        super(id, name, description, entityType, parent, owner);
        this.serverId = serverId;
        this.baseUrl = baseUrl;
        this.adminEmail = adminEmail;
        this.version = version;
        this.password = password;
        this.isDefault = isDefault;
        this.protocol = protocol;
        this.socketsEnabled = socketsEnabled;
    }

    private InstanceModel() {
    }

    @Override
    public long getServerId() {
        return serverId;
    }

    @Override
    public UrlContainer getBaseUrl() {
        return UrlContainer.getInstance(baseUrl);
    }

    @Override
    public EmailAddress getAdminEmail() {
        return CommonFactory.createEmailAddress(adminEmail);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public Protocol getProtocol() {

        return (this.protocol == null || Protocol.valueOf(this.protocol) == null) ? Protocol.http : Protocol.valueOf(this.protocol);
    }

    @Override
    public boolean isSocketsEnabled() {
        return this.socketsEnabled;
    }

    public static class Builder extends EntityBuilder {
        private final EntityType type = EntityType.instance;


        private long serverId;

        private UrlContainer baseUrl;

        private EmailAddress adminEmail;

        private String version;

        private String password;

        private boolean isDefault;

        private Protocol protocol;

        private boolean socketsEnabled;


        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Instance create() {


            if (protocol == null) {
                protocol = Protocol.http;
            }


            return new InstanceModel(id, name, description, type, parent, owner,
                    serverId, baseUrl.getUrl(), adminEmail.getValue(), version, password, isDefault, protocol.name(), socketsEnabled);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Instance e) {
            super.init(e);
            serverId = e.getServerId();

            baseUrl = e.getBaseUrl();

            adminEmail = e.getAdminEmail();

            version = e.getVersion();

            password = e.getPassword();

            isDefault = e.isDefault();

            protocol = e.getProtocol();

            socketsEnabled = e.isSocketsEnabled();

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

        public Builder serverId(long serverId) {
            this.serverId = serverId;
            return this;
        }

        public Builder baseUrl(UrlContainer baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder url(String url) {
            this.baseUrl = UrlContainer.getInstance(url);
            return this;
        }

        public Builder adminEmail(EmailAddress adminEmail) {
            this.adminEmail = adminEmail;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder socketsEnabled(boolean socketsEnabled) {
            this.socketsEnabled = socketsEnabled;
            return this;
        }


    }

}
