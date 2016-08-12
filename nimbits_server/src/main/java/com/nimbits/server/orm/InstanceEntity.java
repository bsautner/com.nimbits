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

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.server.Protocol;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

//TODO - create access keys on the forign instance and use the id of the id entity as the apikey field here
@Deprecated
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class InstanceEntity extends EntityStore implements Instance {

    @NotPersistent
    private long serverId;

    @Persistent
    private String baseUrl;

    @Persistent
    private String adminEmail;

    @Persistent
    private String version;

    @Persistent
    private String password;

    @Persistent
    private boolean isDefault;

    @Persistent
    private String protocol;

    @Persistent
    private boolean socketsEnabled;


    public InstanceEntity(final Instance server) {
        super(server);
        this.serverId = server.getServerId();
        this.baseUrl = server.getBaseUrl().getUrl();
        this.adminEmail = server.getAdminEmail().getValue();
        this.version = server.getVersion();
        this.password = server.getPassword();
        this.isDefault = server.isDefault();
        this.protocol = server.getProtocol().name();
        this.socketsEnabled = server.isSocketsEnabled();

    }

    public InstanceEntity() {
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


    @Override
    public void init(Entity anEntity) {
        // super.init(anEntity);
    }
}
