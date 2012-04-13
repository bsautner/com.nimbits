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

package com.nimbits.server.facebook;

import com.google.gwt.http.client.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.facebook.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.google.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.http.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;


public class FacebookImpl extends RemoteServiceServlet implements FacebookService, RequestCallback

{

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(FacebookImpl.class.getName());

    public EmailAddress facebookLogin(final String code) throws UnsupportedEncodingException, NimbitsException {

        final User u = UserServiceFactory.getInstance().getAppUserUsingGoogleAuth();

        final String facebookClientId = SettingTransactionsFactory.getInstance().getSetting(SettingType.facebookClientId);
        final String facebookSecret = SettingTransactionsFactory.getInstance().getSetting(SettingType.facebookSecret);
        final String redirect_uri = SettingTransactionsFactory.getInstance().getSetting(SettingType.facebookRedirectURL);
        final String token = getToken(code, facebookClientId, redirect_uri, facebookSecret);
        final String jsonEmail = HttpCommonFactory.getInstance().doGet(Path.PATH_FACEBOOK_ME, urlEncodeToken(token) + "&fields=email,name");

        final FacebookUser f = GsonFactory.getSimpleInstance().fromJson(jsonEmail, FacebookUser.class);

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        session.setAttribute(Parameters.email.getText(), u.getEmail());

            u.setFacebookID(f.getId());
            u.setFacebookToken(token);
            EntityServiceFactory.getInstance().addUpdateEntity(u, u);



        updateStatus(token, "Added Nimbits services for facebook.",
                Path.PATH_LOGO_IMG, "http://www.nimbits.com",
                "go to www.nimbits.com to learn more",
                "",
                "Nimbits is a free, social and open source data logging service you can use to integrate your home, apps and life into the cloud."
        );


        return u.getEmail();


    }

    private String getToken(final String code, final String ClientID, final String redirectURL, final String secret) {

        String retStr = null;
        try {
            final String encodedCode = URLEncoder.encode(code, Const.CONST_ENCODING);
            final String u1 = "https://graph.facebook.com/oauth/access_token";
            final String params = "client_id=" + ClientID + "&" +
                    "redirect_uri=" + redirectURL + "&" +
                    "client_secret=" + secret + "&" +
                    "type=user_agent&" +
                    "code=" + encodedCode;

            retStr = HttpCommonFactory.getInstance().doGet(u1, params);
            log.info(retStr);
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
        return retStr;


    }

    private String urlEncodeToken(final String unencodedToken) throws UnsupportedEncodingException {
        final String[] s = unencodedToken.split("=");

        return "access_token=" + URLEncoder.encode(s[1], Const.CONST_ENCODING);


    }


    public String updateStatus(final String token,
                               final String message,
                               final String unEncodedPicture,
                               final String link,
                               final String name,
                               final String captions,
                               final String description) {
        log.info("Facebook status update");
        log.info(token);
        log.info(message);
        log.info(unEncodedPicture);
        log.info(link);
        log.info(name);
        log.info(captions);
        log.info(description);
        //String token = getToken(code);
        String retStr = "";

        String eMessage;
        String ePicture;
        String eLink;
        String eName;
        String eCaptions;
        String eDescription;

        try {

            eMessage = URLEncoder.encode(message, Const.CONST_ENCODING);
            String shortPic = GoogleURLShortener.shortenURL(unEncodedPicture);
            ePicture = URLEncoder.encode(shortPic, Const.CONST_ENCODING);


            String feedURL = "https://graph.facebook.com/me/feed";
            //  URL url = new URL(feedURL);
            //  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setDoOutput(true);
            // connection.setRequestMethod("POST");

            // OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            String params = urlEncodeToken(token);
            params += "&message=" + eMessage;
            params += "&picture=" + ePicture;
            String captions2;

            captions2 = (captions == null) ? "" : captions;


            if (!link.isEmpty()) {
                eLink = URLEncoder.encode(link, Const.CONST_ENCODING);
                params += "&link=" + eLink;
            }
            if (!name.isEmpty()) {
                eName = URLEncoder.encode(name, Const.CONST_ENCODING);
                params += "&name=" + eName;
            }
            if (!captions2.isEmpty()) {
                eCaptions = URLEncoder.encode(captions2, Const.CONST_ENCODING);
                params += "&caption=" + eCaptions;
            }
            if (!description.isEmpty()) {
                eDescription = URLEncoder.encode(description, Const.CONST_ENCODING);
                params += "&description=" + eDescription;
            }
            log.info(feedURL);
            log.info(params);
            HttpCommonFactory.getInstance().doPost(feedURL, params);


        } catch (IOException e) {
            retStr += e.getMessage();
            log.severe(e.getMessage());
        } catch (Exception e) {
            retStr += e.getMessage();
            log.severe(e.getMessage());
        }
        return retStr;


    }


    @Override
    public void onResponseReceived(Request request, Response response) {


    }


    @Override
    public void onError(Request request, Throwable exception) {


    }

}


