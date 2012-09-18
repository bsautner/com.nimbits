/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.external.facebook;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.facebook.FacebookService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.external.google.GoogleURLShortener;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import com.nimbits.server.transactions.service.user.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service("facebookService")
@Transactional
public class FacebookImpl extends RemoteServiceServlet implements FacebookService, RequestCallback

{

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(FacebookImpl.class.getName());
    private static final Pattern COMPILE = Pattern.compile("=");
    private EntityServiceImpl entityService;
    private UserServiceImpl userService;
    private SettingsService settingsService;

    @Override
    public EmailAddress facebookLogin(final String code) throws UnsupportedEncodingException, NimbitsException {

        final User u =userService.getAppUserUsingGoogleAuth();

        final String facebookClientId = settingsService.getSetting(SettingType.facebookClientId);
        final String facebookSecret = settingsService.getSetting(SettingType.facebookSecret);
        final String redirect_uri = settingsService.getSetting(SettingType.facebookRedirectURL);
        final String token = getToken(code, facebookClientId, redirect_uri, facebookSecret);
        final String jsonEmail = HttpCommonFactory.getInstance().doGet(Path.PATH_FACEBOOK_ME, urlEncodeToken(token) + "&fields=email,name");

        final FacebookUser f = GsonFactory.getSimpleInstance().fromJson(jsonEmail, FacebookUser.class);

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        session.setAttribute(Parameters.email.getText(), u.getEmail());

            u.setFacebookID(f.getId());
            u.setFacebookToken(token);
            entityService.addUpdateEntity(u, u);



        updateStatus(token, "Added Nimbits services for facebook.",
                Path.PATH_LOGO_IMG, "http://www.nimbits.com",
                "go to www.nimbits.com to learn more",
                "",
                "Nimbits is a free, social and open source data logging service you can use to integrate your home, apps and life into the cloud."
        );


        return u.getEmail();


    }

    private static String getToken(final String code, final String clientID, final String redirectURL, final String secret) {

        String retStr = null;
        try {
            final String encodedCode = URLEncoder.encode(code, Const.CONST_ENCODING);
            final String u1 = "https://graph.facebook.com/oauth/access_token";
            final String params = "client_id=" + clientID + '&' +
                    "redirect_uri=" + redirectURL + '&' +
                    "client_secret=" + secret + '&' +
                    "type=user_agent&" +
                    "code=" + encodedCode;

            retStr = HttpCommonFactory.getInstance().doGet(u1, params);
            log.info(retStr);
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
        return retStr;


    }

    private static String urlEncodeToken(final CharSequence unencodedToken) throws UnsupportedEncodingException {
        final String[] s = COMPILE.split(unencodedToken);

        return "access_token=" + URLEncoder.encode(s[1], Const.CONST_ENCODING);


    }


    @Override
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

        try {

            String eMessage = URLEncoder.encode(message, Const.CONST_ENCODING);
            String shortPic = GoogleURLShortener.shortenURL(unEncodedPicture);
            String ePicture = URLEncoder.encode(shortPic, Const.CONST_ENCODING);


            //  URL url = new URL(feedURL);
            //  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setDoOutput(true);
            // connection.setRequestMethod("POST");

            // OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            String params = urlEncodeToken(token);
            params += "&message=" + eMessage;
            params += "&picture=" + ePicture;

            String captions2 = captions == null ? "" : captions;


            if (!link.isEmpty()) {
                String eLink = URLEncoder.encode(link, Const.CONST_ENCODING);
                params += "&link=" + eLink;
            }
            if (!name.isEmpty()) {
                String eName = URLEncoder.encode(name, Const.CONST_ENCODING);
                params += "&name=" + eName;
            }
            if (!captions2.isEmpty()) {
                String eCaptions = URLEncoder.encode(captions2, Const.CONST_ENCODING);
                params += "&caption=" + eCaptions;
            }
            if (!description.isEmpty()) {
                String eDescription = URLEncoder.encode(description, Const.CONST_ENCODING);
                params += "&description=" + eDescription;
            }
            String feedURL = "https://graph.facebook.com/me/feed";
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

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }
}


