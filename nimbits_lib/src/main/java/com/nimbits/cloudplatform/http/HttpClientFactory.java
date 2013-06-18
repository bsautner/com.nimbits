package com.nimbits.cloudplatform.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 7:11 PM
 */
public enum HttpClientFactory {
    instance;

    private static DefaultHttpClient httpClient;

    public static DefaultHttpClient getInstance() {
        if (httpClient == null) {
            HttpParams headerParams = new BasicHttpParams();
            headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            httpClient = new DefaultHttpClient(headerParams);

        }
        return httpClient;
    }

    private HttpClientFactory() {
    }
}
