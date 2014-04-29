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

package com.nimbits.client.io;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.List;

public class HttpHelper {

    private final EmailAddress email;
    private final Server server;

    public HttpHelper(EmailAddress email, Server server) {
        this.email = email;
        this.server = server;
    }



    public <T, K> List<T> doGet(final Class<K> clz,
                                       final UrlContainer postUrl,
                                       final List<BasicNameValuePair> parameters,
                                       final Type type,
                                       final boolean expectList

    ) {
        List<T> result;

        result = doHttpGet(clz, postUrl, parameters, type, expectList);


        return result;


    }

    private <T, K> List<T> doHttpGet(Class<K> clz, UrlContainer postUrl, List<BasicNameValuePair> parameters, Type type, boolean expectList) {
        List<T> response = new HttpTransaction(email, server).doGet(clz, postUrl, parameters, type, expectList);
        return response;
    }

    public <T, K>  List<T> doPost(final Class<K> clz,
                                         final UrlContainer postUrl,
                                         final List<BasicNameValuePair> parameters,
                                         final Type type,
                                         final boolean expectList) {

        return new HttpTransaction(email, server).doPost(clz, postUrl, parameters, type, expectList);
    }




}
