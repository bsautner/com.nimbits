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

package com.nimbits.client;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.Style.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.service.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.settings.*;
import com.nimbits.client.service.twitter.*;
import com.nimbits.client.ui.helper.*;
import com.nimbits.client.ui.panels.*;

import java.util.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits extends NavigationEventProvider  implements EntryPoint {


    private LoginInfo loginInfo = null;
    private Viewport viewport;
    private final static String heading = (Const.CONST_SERVER_NAME + " " + SettingType.serverVersion.getDefaultValue());
    private ClientType clientType;


    @Override
    public void onModuleLoad() {
        final String clientTypeParam = Location.getParameter(Const.Params.PARAM_CLIENT);
        String uuid = Location.getParameter(Const.PARAM_UUID);
        final String actionParam = Location.getParameter(Const.Params.PARAM_ACTION);
        final String fb = Location.getParameter(Const.Params.PARAM_FACEBOOK);
        final String code = Location.getParameter(Const.Params.PARAM_CODE);
        final String tw = Location.getParameter(Const.Params.PARAM_TWITTER);
        final String oauth_token = Location.getParameter(Const.Params.PARAM_OAUTH);
        //final String diagramUUID = Location.getParameter(Const.Params.PARAM_DIAGRAM);

//        final String debug = Location.getParameter(Const.PARAM_DEBUG);
        boolean doAndroid = false;

        final boolean doFacebook = ((fb != null) || (code != null));
        final boolean doTwitter = ((tw != null) && (oauth_token == null));
        final boolean doTwitterFinish = ((tw != null) && (oauth_token != null));
        //final boolean doDiagram = (diagramUUID != null);
        boolean doSubscribe = (uuid != null && actionParam != null && actionParam.equals(Action.subscribe.name()));
        Action action = Action.none;

        if (Cookies.getCookieNames().contains(Const.Params.PARAM_CLIENT) && Utils.isEmptyString(clientTypeParam)) {
            clientType = ClientType.valueOf(Cookies.getCookie(Const.Params.PARAM_CLIENT));
        }
        else if (!Utils.isEmptyString(clientTypeParam) && clientTypeParam.equals(Const.WORD_ANDROID)) {
            clientType = ClientType.android;
            doAndroid = true;
        } else {
            clientType = ClientType.other;
        }

        //handles the round trip from login screen.

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

        Cookies.setCookie(Const.Params.PARAM_CLIENT, clientType.name());



        decideWhatViewToLoad(uuid, code, oauth_token, action);

    }


    private void decideWhatViewToLoad(final String uuid,
                                      final String code,
                                      final String oauth_token,
                                      final Action action){
        SettingsServiceAsync settingService = GWT.create(SettingsService.class);
        settingService.getSettings(new AsyncCallback<Map<SettingType, String>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
                if (loginInfo != null) {
                    Window.Location.replace(loginInfo.getLogoutUrl());
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
                    case subscribe: case none:
                        decidedWhatViewToLoadSecondStep(action, settings, uuid);
                        break;
                    default:
                        loadLogin();
                }
            }

        });
    }




    private void decidedWhatViewToLoadSecondStep(final Action action, final Map<SettingType, String> settings, final String uuid)   {
        LoginServiceAsync loginService = GWT
                .create(LoginService.class);
        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(caught.getMessage(), caught);
                        FeedbackHelper.showError(caught);
                    }

                    @Override
                    public void onSuccess(LoginInfo result) {
                        loginInfo = result;
                        if (loginInfo.isLoggedIn()) {
                            switch (action) {
                                case android:  case subscribe: case none:
                                    loadPortalView(loginInfo, action, settings, uuid);
                                    break;
                                case twitter:
                                    doTwitterRedirectForAuthorisation();
                                    break;
                                default:
                                    loadLogin();
                            }
                        } else {
                            if (action.equals(Action.subscribe)) {
                                Cookies.setCookie(Action.subscribe.name(), uuid);
                            }
                            loadLogin();
                        }
                    }

                });
    }

    private void doTwitterRedirectForAuthorisation() {
        final TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        twitterService.twitterAuthorise(loginInfo.getEmailAddress(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(String result) {

                Location.replace(result);
            }

        });
    }



    private void loadPortalView(final LoginInfo loginInfo,
                                final Action action,
                                final Map<SettingType, String> settings,
                                final String uuid)  {


        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);
        final FeedPanel feedPanel = new FeedPanel(loginInfo.getUser());
        //feedPanel.setLayout(new FitLayout());
        //feedPanel.setHeight("100%");

        CenterPanel centerPanel = new CenterPanel(loginInfo, settings);

        centerPanel.addEntityClickedListeners(new EntityClickedListener() {
            @Override
            public void onEntityClicked(GxtModel entity) {
                if (entity.getEntityType().equals(EntityType.feed)) {
                    feedPanel.reload();
                }
            }
        });

        ContentPanel center = new ContentPanel();
        center.setHeading(Const.CONST_SERVER_NAME + " " + SettingType.serverVersion.getDefaultValue());
        center.setScrollMode(Style.Scroll.AUTOX);

        ContentPanel east = new ContentPanel();
        east.setHeading(Const.TEXT_DATA_FEED);
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0,0,5,0));

        BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 250);
        eastData.setSplit(true);
        eastData.setCollapsible(true);
        eastData.setMargins(new Margins(0,0,5,5));

        center.add(centerPanel);
        east.add(feedPanel);

        viewport.add(center, centerData);
        viewport.add(east, eastData);


        if (action.equals(Action.subscribe)) {
            Cookies.removeCookie(Action.subscribe.name());
            showSubscriptionPanel(uuid, settings);
        }
        viewport.setHeight("100%");
        RootPanel.get("main").add(viewport);

    }




    public void showSubscriptionPanel(final String uuid, final Map<SettingType, String> settings) {

        EntityServiceAsync service = GWT.create(EntityService.class);

        service.getEntityByUUID(uuid, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(Entity result) {
                SubscriptionPanel dp = new SubscriptionPanel(result, settings);

                final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
                w.setWidth(500);
                w.setHeight(500);
                w.setHeading("Subscribe");
                w.add(dp);
                dp.addEntityAddedListener(new NavigationEventProvider.EntityAddedListener() {
                    @Override
                    public void onEntityAdded(Entity model) {
                        w.hide();
                        Cookies.removeCookie(Action.subscribe.name());
                        //  mainPanel.addEntity(result);
                        //TODO   mainPanel.addEnToTree(result);

                    }
                });

                w.show();
            }
        });
    }

    private void loadDiagramView(final Entity diagram) {

        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        final ContentPanel contentPanel = new ContentPanel(new FillLayout());
        contentPanel.setHeaderVisible(true);
        contentPanel.setHeading(Const.HTML_HOME_LINK + " | " + heading + " "
                + diagram.getName());
        contentPanel.setFrame(false);


        final DiagramPanel diagramPanel = new DiagramPanel(diagram, false);

        diagramPanel.setHeight("100%");
        contentPanel.add(diagramPanel);
        contentPanel.setLayout(new FillLayout());
        viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
        RootPanel.get().add(viewport);

    }




    private void finishFacebookAuthentication(final Map<SettingType, String> settings, final String code) {
        getViewport();
        FacebookPanel fbPanel = new FacebookPanel(code, settings);
        fbPanel.setHeight(500);
        fbPanel.setWidth(600);
        viewport.add(fbPanel);
        viewport.setWidth(600);
        viewport.setHeight(500);
        RootPanel.get("main").add(viewport);
    }

    private void finishTwitterAuthentication(final Map<SettingType, String> settings, final String oauth_token, final Action action) {
        TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        twitterService.updateUserToken(oauth_token,
                new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        FeedbackHelper.showError(caught);

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.alert(Const.MESSAGE_TWITTER_ADDED);
                        decidedWhatViewToLoadSecondStep(action, settings, null);

                    }

                });

    }

    private void getViewport() {
        viewport = new Viewport();
        viewport.setLayout(new FillLayout());
        viewport.setBorders(false);
    }

    private void loadEntityDisplay(final String uuid) {


        EntityServiceAsync service = GWT.create(EntityService.class);

        service.getEntityByUUID(uuid, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(Entity entity) {
                switch (entity.getEntityType()) {

                    case user:
                        break;
                    case point: case category:
                        Location.replace("report.html?uuid=" + uuid);
                        break;
                    case file:
                        if (EntityOpenHelper.isSVG(entity)) {
                            loadDiagramView(entity);
                        }
                        else {
                            EntityOpenHelper.showBlob(entity);
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
        });



    }

    private void loadLogin() {

        Window.Location.replace(loginInfo.getLoginUrl());

    }



}
