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

package com.nimbits.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.constants.Words;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.file.File;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.settings.SettingsServiceAsync;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.client.service.twitter.TwitterServiceAsync;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.client.ui.helper.EntityOpenHelper;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.*;

import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits extends NavigationEventProvider  implements EntryPoint {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 600;
    private static final int FEED_WIDTH = 250;
    private User loginInfo = null;
    private Viewport viewport;
    private final static String heading = Const.CONST_SERVER_NAME + ' ' + SettingType.serverVersion.getDefaultValue();
    private CenterPanel centerPanel;
    private SettingsServiceAsync settingService;
    private EntityServiceAsync entityService;
    private UserServiceAsync userService;
    private TwitterServiceAsync twitterService;

    private boolean isDomain;

    @Override
    public void onModuleLoad() {

        settingService = GWT.create(SettingsService.class);
        entityService = GWT.create(EntityService.class);
        userService = GWT.create(UserService.class);
        twitterService = GWT.create(TwitterService.class);


        final String clientTypeParam = Location.getParameter(Parameters.client.getText());
        String uuid = Location.getParameter(Parameters.uuid.getText());
        final String actionParam = Location.getParameter(Parameters.action.getText());
        final String fb = Location.getParameter(Parameters.facebook.getText());
        final String code = Location.getParameter(Parameters.code.getText());
        final String tw = Location.getParameter(Parameters.twitter.getText());
        final String oauth_token = Location.getParameter(Parameters.oauth_token.getText());
        final String domainName = Location.getParameter(Parameters.hd.getText());
        isDomain = domainName != null;

        //final String diagramUUID = Location.getParameter(Const.Params.PARAM_DIAGRAM);

//        final String debug = Location.getParameter(Const.PARAM_DEBUG);
        boolean doAndroid = false;

        final boolean doFacebook = fb != null || code != null;
        final boolean doTwitter = tw != null && oauth_token == null;
        final boolean doTwitterFinish = tw != null && oauth_token != null;
        //final boolean doDiagram = (diagramUUID != null);
        final boolean doSubscribe = uuid != null && actionParam != null && actionParam.equals(Action.subscribe.name());

        final ClientType clientType;
        if (Cookies.getCookieNames().contains(Parameters.client.getText()) && Utils.isEmptyString(clientTypeParam)) {
            clientType = ClientType.valueOf(Cookies.getCookie(Parameters.client.getText()));
        }
        else if (!Utils.isEmptyString(clientTypeParam) && clientTypeParam.equals(Words.WORD_ANDROID)) {
            clientType = ClientType.android;
            doAndroid = true;
        } else {
            clientType = ClientType.other;
        }

        //handles the round trip from login screen.

        Action action = Action.none;
        if (doSubscribe && ! Cookies.getCookieNames().contains(Action.subscribe.name())) {

            action = Action.subscribe;
        }
        else if (! doSubscribe && Cookies.getCookieNames().contains(Action.subscribe.name())) {
            uuid = Cookies.getCookie(Action.subscribe.name());
            Cookies.removeCookie(Action.subscribe.name());
            action = Action.subscribe;
        }
        else if (uuid != null && ! doSubscribe) {
            action = Action.report;
        }
        else if (doAndroid) {
            action = Action.android;
        }
        else if (doTwitter) {
            action = Action.twitter;
        }
        else if (doTwitterFinish) {
            action = Action.twitterFinishReg;
        }
        else if (doFacebook) {
            action = Action.facebook;
        }

        Cookies.setCookie(Parameters.client.getText(), clientType.name());



        decideWhatViewToLoad(uuid, code, oauth_token, action);

    }


    private void decideWhatViewToLoad(final String uuid,
                                      final String code,
                                      final String oauth_token,
                                      final Action action){

        settingService.getSettings(new GetSettingMapAsyncCallback(action, uuid, code, oauth_token));
    }

    private void decidedWhatViewToLoadSecondStep(final Action action, final Map<SettingType, String> settings, final String uuid)   {

        userService.login(GWT.getHostPageBaseURL(),
                new LoginInfoAsyncCallback(action, settings, uuid));
    }

    public void showSubscriptionPanel(final User user, final String uuid, final Map<SettingType, String> settings) {


        entityService.getEntityByKey(uuid, EntityType.point , new SubscriptionPanelAsyncCallback(user, settings));
    }

    private void loadLogin(Map<SettingType, String> settings) {


        if (isDomain ) {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/openid");
            try {
                Request response = builder.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        Window.alert(exception.getMessage());
                    }

                    public void onResponseReceived(Request request, Response response) {
                        Window.alert(response.getText());
                    }

            } );
            } catch (RequestException e) {
              FeedbackHelper.showError(e);
            }
        }
        else {
            Window.Location.replace(loginInfo.getLoginUrl());
        }

    }

    private static class TwitterAuthoriseAsyncCallback implements AsyncCallback<String> {

        TwitterAuthoriseAsyncCallback() {
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final String result) {

            Location.replace(result);
        }

    }

    private static class FeedClickedListener implements EntityClickedListener {
        private final FeedPanel feedPanel;

        FeedClickedListener(FeedPanel feedPanel) {
            this.feedPanel = feedPanel;
        }

        @Override
        public void onEntityClicked(final TreeModel entity) {
            if (entity.getEntityType().equals(EntityType.feed)) {
                feedPanel.reload();
            }
        }
    }

    private class SubscriptionPanelAsyncCallback implements AsyncCallback<List<Entity>> {


        private final Map<SettingType, String> settings;
        private final User user;
        SubscriptionPanelAsyncCallback(User user, final Map<SettingType, String> settings) {
            this.settings = settings;
            this.user = user;
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final List<Entity> result) {
            final SubscriptionPanel dp = new SubscriptionPanel(user, result.get(0), settings);

            final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
            w.setWidth(WIDTH);
            w.setHeight(HEIGHT);
            w.setHeading("Subscribe");
            w.add(dp);
            dp.addEntityAddedListener(new SubscriptionEntityAddedListener(w, result));

            w.show();
        }

        private class SubscriptionEntityAddedListener implements EntityAddedListener {
            private final com.extjs.gxt.ui.client.widget.Window w;
            private final List<Entity> result;

            SubscriptionEntityAddedListener(com.extjs.gxt.ui.client.widget.Window w, List<Entity> result) {
                this.w = w;
                this.result = result;
            }

            @Override
            public void onEntityAdded(final Entity model) throws NimbitsException {
                w.hide();
                Cookies.removeCookie(Action.subscribe.name());
                final TreeModel mx = new GxtModel(result.get(0));
                centerPanel.addEntity(mx);
                centerPanel.addEntityToTree(mx);


            }
        }
    }

    private class GetEntityListAsyncCallback implements AsyncCallback<List<Entity>> {
        private final String uuid;

        GetEntityListAsyncCallback(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final List<Entity> r) {
            try {
                if (! r.isEmpty()) {
                    Entity entity = r.get(0);

                    switch (entity.getEntityType()) {

                        case user:
                            break;
                        case point:
                        case category:
                            Location.replace("report.html?uuid=" + uuid);
                            break;
                        case file:
                            if (EntityOpenHelper.isSVG(entity)) {
                                loadDiagramView((File) entity);
                            } else {
                                EntityOpenHelper.showBlob((File) entity);
                            }
                            break;
                        case subscription:
                            break;
                        case userConnection:
                            break;
                        case calculation:
                            break;
                        case intelligence:
                            break;
                        case feed:
                            break;
                    }
                }
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }


        }
        private void loadDiagramView(final File diagram) throws NimbitsException {

            viewport = new Viewport();
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            final ContentPanel contentPanel = new ContentPanel(new FillLayout());
            contentPanel.setHeaderVisible(true);
            contentPanel.setHeading(Const.HTML_HOME_LINK + " | " + heading + ' '
                    + diagram.getName());
            contentPanel.setFrame(false);


            final DiagramPanel diagramPanel = new DiagramPanel(diagram, false);

            diagramPanel.setHeight("100%");
            contentPanel.add(diagramPanel);
            contentPanel.setLayout(new FillLayout());
            viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
            RootPanel.get().add(viewport);

        }
    }

    private class FinishTwitterAsyncCallback implements AsyncCallback<Void> {

        private final Action action;
        private final Map<SettingType, String> settings;

        FinishTwitterAsyncCallback(Action action, Map<SettingType, String> settings) {
            this.action = action;
            this.settings = settings;
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);

        }

        @Override
        public void onSuccess(final Void result) {
            Window.alert(UserMessages.MESSAGE_TWITTER_ADDED);
            decidedWhatViewToLoadSecondStep(action, settings, null);

        }

    }

    private class GetSettingMapAsyncCallback implements AsyncCallback<Map<SettingType, String>> {

        private final Action action;
        private final String uuid;
        private final String code;
        private final String oauth_token;

        GetSettingMapAsyncCallback(Action action, String uuid, String code, String oauth_token) {
            this.action = action;
            this.uuid = uuid;
            this.code = code;
            this.oauth_token = oauth_token;
        }

        @Override
        public void onFailure(final Throwable caught) {
            GWT.log(caught.getMessage(), caught);
            if (loginInfo != null) {
                Location.replace(loginInfo.getLogoutUrl());
            }

        }

        @Override
        public void onSuccess(final Map<SettingType, String> settings) {

            switch (action) {
                case report:
                    loadEntityDisplay(uuid);
                    break;
                case facebook:
                    finishFacebookAuthentication(settings, code);
                    break;
                case twitterFinishReg:
                    finishTwitterAuthentication(settings, oauth_token, action);
                    break;
                case subscribe: case twitter: case android: case none:
                    decidedWhatViewToLoadSecondStep(action, settings, uuid);
                    break;
                default:
                    loadLogin(settings);
            }
        }

        private void finishFacebookAuthentication(final Map<SettingType, String> settings, final String code) {
            getViewport();
            final FacebookPanel fbPanel = new FacebookPanel(code, settings);
            fbPanel.setHeight(HEIGHT);
            fbPanel.setWidth(WIDTH);
            viewport.add(fbPanel);
            viewport.setWidth(WIDTH);
            viewport.setHeight( HEIGHT);
            RootPanel.get("main").add(viewport);
        }
        private void getViewport() {
            viewport = new Viewport();
            viewport.setLayout(new FillLayout());
            viewport.setBorders(false);
        }
        private void finishTwitterAuthentication(final Map<SettingType, String> settings, final String oauth_token, final Action action) {

            twitterService.updateUserToken(oauth_token,
                    new FinishTwitterAsyncCallback(action, settings));

        }
        private void loadEntityDisplay(final String uuid) {



            entityService.getEntityByKey(uuid,EntityType.point , new GetEntityListAsyncCallback(uuid));



        }
    }

    private class LoginInfoAsyncCallback implements AsyncCallback<User> {
        private final Action action;
        private final Map<SettingType, String> settings;
        private final String uuid;

        LoginInfoAsyncCallback(Action action, Map<SettingType, String> settings, String uuid) {
            this.action = action;
            this.settings = settings;
            this.uuid = uuid;
        }

        @Override
        public void onFailure(final Throwable caught) {
            GWT.log(caught.getMessage(), caught);
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final User result) {
            loginInfo = result;
            try {
                if (loginInfo.isLoggedIn()) {
                    switch (action) {
                        case android:  case subscribe: case none:
                            loadPortalView(loginInfo, action, settings, uuid);
                            break;
                        case twitter:
                            doTwitterRedirectForAuthorisation();
                            break;
                        default:
                            loadLogin(settings);
                    }
                } else {
                    if (action.equals(Action.subscribe)) {
                        Cookies.setCookie(Action.subscribe.name(), uuid);
                    }
                    loadLogin(settings);
                }
            } catch (NimbitsException ex) {
                FeedbackHelper.showError(ex);
            }
        }

        private void doTwitterRedirectForAuthorisation() throws NimbitsException {

            twitterService.twitterAuthorise(loginInfo.getEmail(), new TwitterAuthoriseAsyncCallback());
        }
        private void loadPortalView(final User loginInfo,
                                    final Action action,
                                    final Map<SettingType, String> settings,
                                    final String uuid)  {


            viewport = new Viewport();
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            //feedPanel.setLayout(new FitLayout());
            //feedPanel.setHeight("100%");

            centerPanel = new CenterPanel(loginInfo, settings, action, isDomain);


            final ContentPanel center = new ContentPanel();
            center.setHeading(Const.CONST_SERVER_NAME + ' ' + SettingType.serverVersion.getDefaultValue());
            center.setScrollMode(Style.Scroll.AUTOX);

            UserServiceAsync service = GWT.create(UserService.class);
            service.getQuota(new AsyncCallback<Integer>() {
                @Override
                public void onFailure(Throwable caught) {
                    FeedbackHelper.showError(caught);
                }

                @Override
                public void onSuccess(Integer result) {
                    if (result > 0) {
                        center.setHeading(Const.CONST_SERVER_NAME + ' ' + SettingType.serverVersion.getDefaultValue() + " API Calls: " + result );
                    }
                }
            });


            final ContentPanel east = new ContentPanel();
            east.setHeading(Const.TEXT_DATA_FEED);
            final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
            centerData.setMargins(new Margins(0,0,5,0));

            final BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, FEED_WIDTH);
            eastData.setSplit(true);
            eastData.setCollapsible(true);
            eastData.setMargins(new Margins(0,0,5,5));

            center.add(centerPanel);


            viewport.add(center, centerData);
            if (! action.equals(Action.android)) {
                final FeedPanel feedPanel = new FeedPanel(loginInfo);
                centerPanel.addEntityClickedListeners(new FeedClickedListener(feedPanel));
                east.add(feedPanel);
                viewport.add(east, eastData);
            }


            if (action.equals(Action.subscribe)) {
                Cookies.removeCookie(Action.subscribe.name());
                showSubscriptionPanel(loginInfo, uuid, settings);
            }
            viewport.setHeight("100%");
            RootPanel.get("main").add(viewport);

        }

    }
}
