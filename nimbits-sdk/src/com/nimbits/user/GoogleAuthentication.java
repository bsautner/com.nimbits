/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.exceptions.GoogleAuthenticationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class GoogleAuthentication {
    private EmailAddress email;
    private String secret;
    private Cookie authCookie = null;

    public class InvalidTokenException extends Exception {

        private String _message;

        private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;
        ;

        InvalidTokenException() {
            setMessage("Internal Server Error 500");
        }

        private void setMessage(final String message) {
            this._message = message;
        }

        public String getMessage() {
            return _message;
        }

//
    }


    public Cookie getAuthCookie() {
        return authCookie;
    }

    public static GoogleAuthentication getNewGoogleAuthentication() {

        ref = new GoogleAuthentication();
        return ref;
    }

    public static GoogleAuthentication getGoogleAuthentication() {
        if (ref == null) {
            ref = new GoogleAuthentication();
        }
        return ref;
    }

    private GoogleAuthentication() {

    }

    @Override
    public Object clone()
            throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
        // that'll teach 'em
    }

    private static GoogleAuthentication ref;

    public boolean Connect(final String gaeAppBaseUrl,
                           final EmailAddress googleAccount,
                           final String googlePassword) throws NimbitsException {
        boolean retVal = true;

        final String gaeAppLoginUrl = gaeAppBaseUrl + Const.PATH_AH_LOGIN;

        final String authToken = getToken(googleAccount, googlePassword);
        try {
            authCookie = getAuthCookie(gaeAppBaseUrl, gaeAppLoginUrl, authToken);
        } catch (Exception e) {
            throw new NimbitsException(e.getMessage());
        }


        return retVal;
    }

    //Alternate Method of connecting - supports android phones that already have the token
    public Cookie ConnectAuth(final String authToken,
                              final String gaeAppBaseUrl) throws GoogleAuthenticationException, NimbitsException {
        final String gaeAppLoginUrl = gaeAppBaseUrl + Const.PATH_AH_LOGIN;
        authCookie = getAuthCookie(gaeAppBaseUrl, gaeAppLoginUrl, authToken);
        if (authCookie == null) {
            throw new GoogleAuthenticationException("Null Cookie returned using " + gaeAppLoginUrl + " and token: " + authToken);

        }
        return authCookie;
    }


    private String getToken(final EmailAddress googleAccount,
                            final String googlePassword) throws NimbitsException {

        final HttpURLConnection h;
        try {
            h = getConnection(
                    Const.CONST_AH, googleAccount, googlePassword);
            return extractAuthTokenFromResponse(h);
        } catch (IOException e) {
            throw new NimbitsException(e.getMessage());
        }


    }


    private Cookie getAuthCookie(final String gaeAppBaseUrl,
                                 final String gaeAppLoginUrl,
                                 final String authToken) throws NimbitsException {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        Cookie retObj = null;
        final String cookieUrl;
        try {
            cookieUrl = gaeAppLoginUrl + "?continue="
                    + URLEncoder.encode(gaeAppBaseUrl, Const.CONST_ENCODING) + "&auth=" + URLEncoder.encode
                    (authToken, Const.CONST_ENCODING);

            //
            //		String cookieUrl = gaeAppLoginUrl + "?continue="
            //		+ URLEncoder.encode(gaeAppBaseUrl,Const.CONST_ENCODING) + "&auth=" + authToken;
            //httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
            final HttpGet httpget = new HttpGet(cookieUrl);
            final HttpResponse response = httpClient.execute(httpget);

            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK ||
                    response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {

                for (final Cookie cookie : httpClient.getCookieStore().getCookies()) {
                    if (cookie.getName().equals(Const.PARAM_ACSID)) {
                        retObj = cookie;
                        break;
                    }

                }
            } else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                throw new NimbitsException("invalid token");

            } else {
                throw new NimbitsException("Error getting cookie: status code:" + response.getStatusLine().getStatusCode());
            }

            httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        } catch (UnsupportedEncodingException e) {
            throw new NimbitsException(e.getMessage());
        } catch (ClientProtocolException e) {
            throw new NimbitsException(e.getMessage());
        } catch (IOException e) {
            throw new NimbitsException(e.getMessage());
        }

        return retObj;

    }

    private HttpURLConnection getConnection(
            final String service,
            final EmailAddress username,
            final String password) throws IOException {

        final URL url = new URL(Const.PATH_GOOGLE_CLIENT_LOGIN);
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty(Const.Params.PARAM_CONTENT_TYPE,
                "application/x-www-form-urlencoded");
        final StringBuilder content = new StringBuilder();
        content.append("Email=")
                .append(username.getValue())
                .append("&Passwd=")
                .append(password)
                .append("&service=")
                .append(service);

        final OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(content.toString().getBytes(Const.CONST_ENCODING));
        outputStream.close();
        return urlConnection;
    }

    private String extractAuthTokenFromResponse(final HttpURLConnection urlConnection)
            throws IOException {
        int responseCode = urlConnection.getResponseCode();

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
        return resp.toString();
    }


    public void setSecret(final String secret) {
        this.secret = secret;
    }


    public String getSecret() {
        return secret;
    }


    public void setEmail(final EmailAddress email) {
        this.email = email;
    }


    public EmailAddress getEmail() {
        return email;
    }
}
