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

package com.nimbits.cloudplatform.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.settings.SettingsService;
import com.nimbits.cloudplatform.client.service.settings.SettingsServiceAsync;
import com.nimbits.cloudplatform.client.service.user.UserService;
import com.nimbits.cloudplatform.client.service.user.UserServiceAsync;
import com.nimbits.cloudplatform.client.ui.helper.FeedbackHelper;
import com.nimbits.cloudplatform.client.ui.panels.CenterPanel;
import com.nimbits.cloudplatform.client.ui.panels.NavigationEventProvider;

import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits extends NavigationEventProvider implements EntryPoint {

    private User loginInfo = null;
    private UserServiceAsync userService;


    @Override
    public void onModuleLoad() {

        SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
        userService = GWT.create(UserService.class);
        settingsService.getSettingsRpc(new GetSettingMapAsyncCallback());

    }




    private void loadLogin() {


        Location.replace(loginInfo.getLoginUrl());


    }


    private class GetSettingMapAsyncCallback implements AsyncCallback<Map<String, String>> {

        GetSettingMapAsyncCallback() {

        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
            if (loginInfo != null) {
                Location.replace(loginInfo.getLogoutUrl());
            }

        }

        @Override
        public void onSuccess(final Map<String, String> settings) {


            userService.loginRpc(GWT.getHostPageBaseURL(),
                    new LoginInfoAsyncCallback(settings));

        }
    }


    private class LoginInfoAsyncCallback implements AsyncCallback<User> {

        private final Map<String, String> settings;


        LoginInfoAsyncCallback(Map<String, String> settings) {

            this.settings = settings;

        }

        @Override
        public void onFailure(final Throwable caught) {

            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final User result) {
            loginInfo = result;
            try {
                if (loginInfo.isLoggedIn()) {

                    loadPortalView(loginInfo, settings);

                }
                else {

                    loadLogin();
                }
            } catch (Exception ex) {
                FeedbackHelper.showError(ex);
            }
        }



        private void loadPortalView(final User loginInfo,

                                    final Map<String, String> settings
                                   ) {


            Viewport viewport = new Viewport();
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            //feedPanel.setLayout(new FitLayout());
            //feedPanel.setHeight("100%");

            CenterPanel centerPanel = new CenterPanel(loginInfo, settings);


            ContentPanel center = new ContentPanel();
            center.setHeading(Const.CONST_SERVER_NAME + ' ' + SettingType.serverVersion.getDefaultValue());
            center.setScrollMode(Style.Scroll.AUTOX);


            final ContentPanel east = new ContentPanel();
            east.setHeading(Const.TEXT_DATA_FEED);
            final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
            centerData.setMargins(new Margins(0, 0, 5, 0));


            center.add(centerPanel);


            viewport.add(center, centerData);


            viewport.setHeight("100%");
            RootPanel.get("main").add(viewport);

        }

    }


}
