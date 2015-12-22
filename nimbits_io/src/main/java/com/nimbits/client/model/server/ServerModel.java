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

import com.google.gson.annotations.Expose;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


public class ServerModel implements Server, Serializable {
    @Expose
    private UrlContainer url;
    @Expose
    private AccessKey accessToken;
    @Expose
    private Protocol protocol;
    @Expose
    private String email;


    public ServerModel(final UrlContainer url, EmailAddress emailAddress, final AccessKey accessToken) {
        if (StringUtils.isEmpty(url.getUrl())) {
            throw new IllegalArgumentException("url was null");
        }
        this.url = removeProtocol(url);

        this.accessToken = accessToken;

        this.protocol = Protocol.http;
        this.email = emailAddress.getValue();
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
    public AccessKey getAccessToken() {
        return accessToken;
    }


    @Override
    public Protocol getProtocol() {
        return protocol;
    }


    @Override
    public EmailAddress getEmail() {
        return CommonFactory.createEmailAddress(email);
    }

}
