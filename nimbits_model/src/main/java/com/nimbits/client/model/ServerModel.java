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

package com.nimbits.client.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by benjamin on 10/20/13.
 */
public class ServerModel implements Server, Serializable {

    private String url;
    private long id;
    private String apiKey;
    private boolean isDefault;

    public ServerModel(final String url, final long id, final String apiKey, boolean isDefault) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url was null");
        }
        this.url = removeProtocol(url);

        this.id = id;
        this.apiKey = apiKey;
        this.isDefault = isDefault;
    }

    protected String removeProtocol(String url) {
        return url.replace("http://", "").replace("https://", "");
    }

    protected ServerModel() {
    }

    @Override
    public String getUrl() {
        return removeProtocol(this.url);

    }



    @Override
    public long getId() {
        return id;
    }
    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }
}
