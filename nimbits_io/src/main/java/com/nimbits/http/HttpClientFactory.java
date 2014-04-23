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

package com.nimbits.http;

import com.nimbits.client.enums.SettingType;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 7:11 PM
 */
public enum HttpClientFactory {
    instance;

    private static DefaultHttpClient httpClient;

    public static DefaultHttpClient getInstance(final String apiKey) {
        if (httpClient == null) {
            HttpParams headerParams = new BasicHttpParams();
            headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            if (apiKey != null) {
                headerParams.setParameter(SettingType.apiKey.getName(), apiKey);
            }
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(headerParams, timeoutConnection);

            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(headerParams, timeoutSocket);

            httpClient = new DefaultHttpClient(headerParams);


        }
        return httpClient;
    }

    private HttpClientFactory() {
    }
}
