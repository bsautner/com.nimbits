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

package com.nimbits.server.external.twitter;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.twitter.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.settings.*;
import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;

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
        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterClientId);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterSecret);
        final Twitter twitter = new TwitterFactory().getInstance();
        log.info("Authorising Twitter");
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);

        String authUrl = null;
        try {
            final RequestToken requestToken = twitter.getOAuthRequestToken();

            authUrl = requestToken.getAuthorizationURL();

            final HttpServletRequest request = this.getThreadLocalRequest();
            final HttpSession session = request.getSession();

            session.setAttribute(Parameters.rToken.getText(), requestToken);
            session.setAttribute(Parameters.email.getText(), email);

        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return authUrl;


    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void updateUserToken(final String token) throws NimbitsException {
        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterClientId);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterSecret);

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();

        final Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);

        final RequestToken requestToken = (RequestToken) session.getAttribute(Parameters.rToken.getText());
        final CommonIdentifier email = (CommonIdentifier) session.getAttribute(Parameters.email.getText());

        log.info("Twitter: Updating user token " + email.getValue() + "  " + request);

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
            List<Entity> result = EntityServiceFactory.getInstance().getEntityByKey(email.getValue(), EntityType.user);
            if (! result.isEmpty()) {
                User u = (User) result.get(0);
                u.setTwitterToken(accessToken.getToken());
                u.setTwitterTokenSecret(accessToken.getTokenSecret());
                EntityServiceFactory.getInstance().addUpdateEntity(u, u);
            }


            sendTweet("Added #Nimbits Data Logger. A free, social and open source data logging service.", accessToken.getToken(), accessToken.getTokenSecret());

        } catch (TwitterException e) {
            log.severe(e.getMessage());
        }

    }

    @Override
    public void sendTweet(final User u, final String message)  {


        try {
            final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterClientId);

            final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterSecret);
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

    static void sendTweet(final String message, final String token, final String secret) throws NimbitsException {

        log.info("Sending tweet");
        log.info(token);
        log.info(secret);

        final String twitter_client_id = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterClientId);
        final String twitter_Secret = SettingTransactionsFactory.getInstance().getSetting(SettingType.twitterSecret);


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

