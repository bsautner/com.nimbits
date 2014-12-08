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

package com.nimbits.client.model.instance;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.server.Protocol;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.client.model.server.apikey.ApiKeyFactory;

import java.io.Serializable;


public class InstanceModel extends EntityModel implements Serializable, Instance {


    private long serverId;

    private String baseUrl;

    private String adminEmail;

    private String version;

    private String  apiKey;

    private boolean isDefault;

    private String protocol;

    private boolean socketsEnabled;


    public InstanceModel(final Entity baseEntity, final String baseUrl, final EmailAddress ownerEmail, final String serverVersion)  {
        super(baseEntity);
        this.baseUrl = baseUrl;
        this.adminEmail = ownerEmail.getValue();
        this.version = serverVersion;
    }

    public InstanceModel(final Instance server)  {
        super(server);
        this.serverId = server.getServerId();
        this.baseUrl = server.getBaseUrl().getUrl();
        this.adminEmail = server.getAdminEmail().getValue();
        this.version = server.getVersion();
        this.apiKey = server.getApiKey().getValue();
        this.isDefault = server.isDefault();
        this.protocol = server.getProtocol().name();
        this.socketsEnabled = server.isSocketsEnabled();


    }

    public InstanceModel() {
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
    public EmailAddress getAdminEmail()  {
        return CommonFactory.createEmailAddress(adminEmail);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ApiKey getApiKey() {
        return ApiKeyFactory.createApiKey(this.apiKey);
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

}
