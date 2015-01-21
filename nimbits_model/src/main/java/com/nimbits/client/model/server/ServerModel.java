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

package com.nimbits.client.model.server;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.client.model.server.apikey.ApiKeyFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


public class ServerModel implements Server, Serializable {

    private UrlContainer url;
    private long id;
    private ApiKey apiKey;
    private boolean isDefault;
    private Protocol protocol;
    private boolean socketsEnabled;



    public ServerModel(final UrlContainer url, final long id, final ApiKey apiKey, boolean socketsEnabled,  boolean isDefault) {
        if (StringUtils.isEmpty(url.getUrl())) {
            throw new IllegalArgumentException("url was null");
        }
        this.url = removeProtocol(url);

        this.id = id;
        this.apiKey = apiKey;
        this.isDefault = isDefault;
        this.protocol = Protocol.http;
        this.socketsEnabled = socketsEnabled;
    }


    public ServerModel(final UrlContainer url, final ApiKey apiKey) {
        if (StringUtils.isEmpty(url.getUrl())) {
            throw new IllegalArgumentException("url was null");
        }
        this.url = removeProtocol(url);

        this.id = 0;
        this.apiKey = apiKey;
        this.isDefault = true;
        this.protocol = Protocol.http;
    }

    public ServerModel(final UrlContainer url) {
        if (StringUtils.isEmpty(url.getUrl())) {
            throw new IllegalArgumentException("url was null");
        }
        this.url = removeProtocol(url);

        this.id = 0;
        this.apiKey = ApiKeyFactory.createEmptyKey();
        this.isDefault = true;
        this.protocol = Protocol.http;
    }

    protected UrlContainer removeProtocol(UrlContainer url) {
        return UrlContainer.getInstance(url.getUrl().replace("http://", "").replace("https://", ""));
    }

    protected ServerModel() {
    }

    @Override
    public String getUrl() {
        return removeProtocol(this.url).getUrl();

    }



    @Override
    public long getId() {
        return id;
    }

    @Override
    public ApiKey getApiKey() {
        return apiKey;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public boolean isSocketsEnabled() {
        return socketsEnabled;
    }
}
