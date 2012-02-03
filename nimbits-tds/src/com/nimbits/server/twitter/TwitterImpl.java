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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.twitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.user.UserTransactionFactory;
import com.nimbits.shared.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 5:09 PM
 */
public class TwitterImpl extends RemoteServiceServlet implements
        TwitterService, RequestCallback {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TwitterImpl.class.getName());

    @Override
    public void onResponseReceived(Request request, Response response) {

    }

    @Override
    public void onError(Request request, Throwable exception) {


    }

    @Override
    public String twitterAuthorise(final EmailAddress email) throws NimbitsException {
        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_CLIENT_ID);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_SECRET);
        final Twitter twitter = new TwitterFactory().getInstance();
        log.info("Authorising Twitter");
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);

        final RequestToken requestToken;
        String authUrl = null;
        try {
            requestToken = twitter.getOAuthRequestToken();

            authUrl = requestToken.getAuthorizationURL();

            final HttpServletRequest request = this.getThreadLocalRequest();
            final HttpSession session = request.getSession();

            session.setAttribute(Const.PARAM_TOKEN, requestToken);
            session.setAttribute(Const.PARAM_EMAIL, email);

        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return authUrl;


    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void updateUserToken(final String token) throws NimbitsException {
        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_CLIENT_ID);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_SECRET);

        AccessToken accessToken;

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();

        final Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);

        final RequestToken requestToken = (RequestToken) session.getAttribute(Const.PARAM_TOKEN);
        final EmailAddress email = (EmailAddress) session.getAttribute(Const.PARAM_EMAIL);

        log.info("Twitter: Updating user token " + email.getValue() + "  " + request);

        try {
            accessToken = twitter.getOAuthAccessToken(requestToken);

            UserTransactionFactory.getInstance().updateTwitter(email, accessToken);

            sendTweet("Added #Nimbits Data Logger. A free, social and open source data logging service.", accessToken.getToken(), accessToken.getTokenSecret());

        } catch (TwitterException e) {
            log.severe(e.getMessage());
        }

    }

    public void sendTweet(final User u, final String message)  {


        final String twitter_client_id;
        try {
            twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_CLIENT_ID);

        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_SECRET);
        if (u != null && ! Utils.isEmptyString(u.getTwitterToken()) && ! Utils.isEmptyString(u.getTwitterTokenSecret())) {
            final AccessToken accessToken = new AccessToken(u.getTwitterToken(),
                    u.getTwitterTokenSecret());


            final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
            confbuilder.setOAuthAccessToken(accessToken.getToken())
                    .setOAuthAccessTokenSecret(accessToken.getTokenSecret())
                    .setOAuthConsumerKey(twitter_client_id)
                    .setOAuthConsumerSecret(twitter_Secret);
            Twitter twitter = new TwitterFactory(confbuilder.build()).getInstance();

            try {
                twitter.updateStatus(message);
            } catch (TwitterException e) {
                GWT.log(e.getMessage(), e);
            }
        }
        } catch (NimbitsException e) {
           log.severe(e.getMessage());
        }
    }

    void sendTweet(final String message, final String token, final String secret) throws NimbitsException {

        log.info("Sending tweet");
        log.info(token);
        log.info(secret);

        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_CLIENT_ID);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_TWITTER_SECRET);


        log.info(twitter_client_id);
        log.info(twitter_Secret);


        final AccessToken accessToken = new AccessToken(token, secret);
        final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        confbuilder.setOAuthAccessToken(accessToken.getToken())
                .setOAuthAccessTokenSecret(accessToken.getTokenSecret())
                .setOAuthConsumerKey(twitter_client_id)
                .setOAuthConsumerSecret(twitter_Secret);
        Twitter twitter = new TwitterFactory(confbuilder.build()).getInstance();

        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            log.severe(e.getMessage());
        }
    }


}

