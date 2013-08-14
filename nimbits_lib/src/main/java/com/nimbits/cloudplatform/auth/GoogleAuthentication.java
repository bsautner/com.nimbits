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

package com.nimbits.cloudplatform.auth;

import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.http.HttpHelper;
import com.nimbits.cloudplatform.http.UrlContainer;
import org.apache.http.cookie.Cookie;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


public class GoogleAuthentication {

    private static final String CONST_AH = "ah";
    private static final String CONST_ENCODING = "UTF-8";
    private static final UrlContainer PATH_AH_LOGIN = UrlContainer.getInstance("/_ah/login");


    private GoogleAuthentication() {

    }

    @Override
    public Object clone()
            throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
        // that'll teach 'em
    }


    public static List<Cookie> getAuthCookies(final UrlContainer gaeAppBaseUrl,
                                              final SimpleValue googleAccount,
                                              final SimpleValue googlePassword) {


        final UrlContainer gaeAppLoginUrl = UrlContainer.combine(gaeAppBaseUrl, PATH_AH_LOGIN);

        final SimpleValue authToken = getToken(googleAccount, googlePassword);

        return HttpHelper.getAuthCookie(gaeAppLoginUrl, authToken.toString(), gaeAppBaseUrl.getUrl());

    }

//    //Alternate Method of connecting - supports com.nimbits.cloudplatform.android phones that already have the token
//    public List<Cookie> connectAuth(final SimpleValue<String> authToken,
//                                    final UrlContainer gaeAppBaseUrl) {
//        final UrlContainer gaeAppLoginUrl = UrlContainer.getInstance(gaeAppBaseUrl + Path.PATH_AH_LOGIN);
//        List<Cookie> authCookie = HttpHelper.getAuthCookie(gaeAppLoginUrl, authToken);
//        return authCookie;
//    }


    private static SimpleValue<String> getToken(final SimpleValue googleAccount,
                                                final SimpleValue googlePassword) {

        final HttpURLConnection h = getConnection(
                CONST_AH, googleAccount, googlePassword);


        SimpleValue<String> r = extractAuthTokenFromResponse(h);
        return r;


    }


    private static HttpURLConnection getConnection(
            final String service,
            final SimpleValue<String> username,
            final SimpleValue<String> password) {

        final URL url;
        try {
            url = new URL(Path.PATH_GOOGLE_CLIENT_LOGIN);

            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty(Parameters.contentType.getText(),
                    "application/x-www-form-urlencoded");
            final StringBuilder content = new StringBuilder();
            content.append("Email=")
                    .append(username.getValue())
                    .append("&Passwd=")
                    .append(password.toString())
                    .append("&service=")
                    .append(service);

            final OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(content.toString().getBytes(CONST_ENCODING));
            outputStream.close();
            return urlConnection;
        } catch (MalformedURLException e) {

            return null;

        } catch (ProtocolException e) {
            return null;


        } catch (UnsupportedEncodingException e) {
            return null;


        } catch (IOException e) {
            return null;


        }


    }

    private static SimpleValue<String> extractAuthTokenFromResponse(final HttpURLConnection urlConnection) {
        int responseCode = 0;
        try {
            responseCode = urlConnection.getResponseCode();


            final StringBuilder resp = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line;


                while ((line = rd.readLine()) != null) {

                    if (line.startsWith("Auth=")) {
                        resp.append(line.substring(5));

                    }

                }

                rd.close();


            }
            return SimpleValue.getInstance(resp.toString());
        } catch (IOException e) {
            return SimpleValue.getEmptyInstance();

        }

    }


}
