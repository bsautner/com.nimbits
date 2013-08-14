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

package com.nimbits.cloudplatform.http;

import com.google.gson.Gson;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Benjamin Sautner
 * Date: 1/18/13
 * Time: 7:01 AM
 */
@SuppressWarnings({"unchecked", "unused"})
public class HttpHelper {



    static {


    }

    public static void init(Cookie authCookie, Gson aGson) {

        HttpTransaction.init(authCookie, aGson);
    }



    private static String buildCode(final UrlContainer postUrl,
                                    final List<BasicNameValuePair> parameters
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append(postUrl.getUrl().hashCode());
        for (BasicNameValuePair pair : parameters) {
            sb.append(pair.getName().hashCode()).append(pair.getValue().hashCode());

        }
        return  sb.toString();

    }



    public static <T, K> List<T> doGet(final Class<K> clz,
                                       final UrlContainer postUrl,
                                       final List<BasicNameValuePair> parameters,
                                       final Type type,
                                       final boolean expectList

    ) {
        List<T> result;
        String code = buildCode(postUrl, parameters);
        result = doHttpGet(clz, postUrl, parameters, type, expectList, code);


        return result;


    }

    private static <T, K> List<T> doHttpGet(Class<K> clz, UrlContainer postUrl, List<BasicNameValuePair> parameters, Type type, boolean expectList, String code) {
        List<T> response = HttpTransaction.doGet(clz, postUrl, parameters, type, expectList);
        return response;
    }

    public static <T, K>  List<T> doPost(final Class<K> clz,
                                         final UrlContainer postUrl,
                                         final List<BasicNameValuePair> parameters,
                                         final Type type,
                                         final FlushType flushType,
                                         final boolean expectList) {

        return HttpTransaction.doPost(clz, postUrl, parameters, type, expectList);
    }


    public static List<Cookie> getAuthCookie(final UrlContainer gaeAppLoginUrl,
                                             final String authToken,
                                             final String baseUrl) {
        return HttpTransaction.getAuthCookie(gaeAppLoginUrl, authToken, baseUrl);

    }

}
