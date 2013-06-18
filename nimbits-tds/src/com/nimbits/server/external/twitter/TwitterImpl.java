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

package com.nimbits.server.external.twitter;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.twitter.TwitterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 5:09 PM
 */
@Service("twitterService")
@Transactional

public class TwitterImpl extends RemoteServiceServlet implements
        TwitterService, RequestCallback {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TwitterImpl.class.getName());
    private EntityService entityService;
    private SettingsService settingsService;
    private MemcacheService cacheFactory;


    @Override
    public void onResponseReceived(Request request, Response response) {

    }

    @Override
    public void onError(Request request, Throwable exception) {


    }

    @Override
    public String twitterAuthorise(final EmailAddress email) throws NimbitsException {
        final String twitter_client_id = settingsService.getSetting(SettingType.twitterClientId);
        final String twitter_Secret = settingsService.getSetting(SettingType.twitterSecret);
        final Twitter twitter = new TwitterFactory().getInstance();
        log.info("Authorising Twitter");
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);
        RequestToken requestToken;

        try {
            requestToken = twitter.getOAuthRequestToken();
            cacheFactory.put(MemCacheKey.twitter.getText() + email.getValue(), requestToken);

            return  requestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            throw new NimbitsException(e);
        }





    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void updateUserToken(final User user, final String token) throws NimbitsException {
        final String twitter_client_id = settingsService.getSetting(SettingType.twitterClientId);
        final String twitter_Secret =settingsService.getSetting(SettingType.twitterSecret);

        final Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitter_client_id, twitter_Secret);


        // = (RequestToken) session.getAttribute(Parameters.rToken.getText());
//        final CommonIdentifier email = (CommonIdentifier) session.getAttribute(Parameters.email.getText());

        //  log.info("Twitter: Updating user token " + email.getValue() + "  " + request);

        try {


            if (cacheFactory.contains(MemCacheKey.twitter.getText() + user.getKey())) {
                RequestToken requestToken = (RequestToken) cacheFactory.get(MemCacheKey.twitter.getText() + user.getKey());
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
                List<Entity> result = entityService.getEntityByKey(user, user.getKey(), EntityType.user);
                if (! result.isEmpty()) {
                    User u = (User) result.get(0);
                    u.setTwitterToken(accessToken.getToken());
                    u.setTwitterTokenSecret(accessToken.getTokenSecret());
                    entityService.addUpdateEntity(u, u);
                }

                cacheFactory.delete(MemCacheKey.twitter.getText() + user.getKey());
                sendTweet("Added #Nimbits Data Logger. A free, social and open source data logging service.", accessToken.getToken(), accessToken.getTokenSecret());
            }
        } catch (TwitterException e) {
            log.severe(e.getMessage());
        }

    }

    @Override
    public void sendTweet(final User u, final String message)  {


        try {
            final String twitter_client_id = settingsService.getSetting(SettingType.twitterClientId);

            final String twitter_Secret = settingsService.getSetting(SettingType.twitterSecret);
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

    private void sendTweet(final String message, final String token, final String secret) throws NimbitsException {

        log.info("Sending tweet");
        log.info(token);
        log.info(secret);

        final String twitter_client_id = settingsService.getSetting(SettingType.twitterClientId);
        final String twitter_Secret = settingsService.getSetting(SettingType.twitterSecret);


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


    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setCacheFactory(MemcacheService cacheFactory) {
        this.cacheFactory = cacheFactory;
    }
}

