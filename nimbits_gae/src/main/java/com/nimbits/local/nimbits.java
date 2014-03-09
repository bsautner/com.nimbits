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

package com.nimbits.local;

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
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.CenterPanel;
import com.nimbits.client.ui.panels.NavigationEventProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits extends NavigationEventProvider implements EntryPoint {

    private User loginInfo = null;
    private UserServiceAsync userService;


    @Override
    public void onModuleLoad() {


        userService = GWT.create(UserService.class);
        userService.loginRpc(GWT.getHostPageBaseURL(),
                new LoginInfoAsyncCallback());

    }


    private void loadLogin() {


        Location.replace(loginInfo.getLoginUrl());


    }


    private class LoginInfoAsyncCallback implements AsyncCallback<User> {

        LoginInfoAsyncCallback() {


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

                    loadPortalView(loginInfo);

                } else {

                    loadLogin();
                }
            } catch (Exception ex) {
                FeedbackHelper.showError(ex);
            }
        }


        private void loadPortalView(final User loginInfo) {


            Viewport viewport = new Viewport();
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            //feedPanel.setLayout(new FitLayout());
            //feedPanel.setHeight("100%");

            CenterPanel centerPanel = new CenterPanel(loginInfo);


            ContentPanel center = new ContentPanel();
            center.setHeadingText("Nimbits Admin Console 3.5.5");
            center.setScrollMode(Style.Scroll.AUTOX);


            final ContentPanel east = new ContentPanel();
            // east.setHeading(Const.TEXT_DATA_FEED);
            final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
            centerData.setMargins(new Margins(0, 0, 5, 0));


            center.add(centerPanel);


            viewport.add(center, centerData);


            viewport.setHeight("100%");
            RootPanel.get("main").add(viewport);

        }

    }


}
