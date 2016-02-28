/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.system.SystemDetails;
import com.nimbits.client.model.system.SystemDetailsModel;
import com.nimbits.client.model.user.LoginInfo;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.service.user.UserServiceRpcException;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.CenterPanel;
import com.nimbits.client.ui.panels.NavigationEventProvider;
import com.nimbits.client.ui.panels.login.LoginListener;
import com.nimbits.client.ui.panels.login.LoginMainPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class NimbitsEntryPoint extends NavigationEventProvider implements EntryPoint, LoginListener {

    private static final String MAIN = "main";

    private static final String HEIGHT = "100%";
    private LoginMainPanel loginMainPanel;
    private boolean connectionApproved = false;
    private boolean isAppEngine = false;
    private SystemDetails systemDetails = new SystemDetailsModel(Const.VERSION, isAppEngine);


    @Override
    public void onModuleLoad() {
        final UserServiceRpcAsync userService = GWT.create(UserServiceRpc.class);

        final String passwordResetToken = Window.Location.getParameter(Parameters.rToken.getText());
        final String connectionRequestToken = Window.Location.getParameter(Parameters.connectionId.getText());
        final String email = Window.Location.getParameter(Parameters.email.getText());

        if (passwordResetToken == null && !doConnection(connectionRequestToken, email)) {
            userService.getSystemDetails(new AsyncCallback<SystemDetails>() {
                @Override
                public void onFailure(Throwable throwable) {
                    loadLoginView(UserModelFactory.createNullLoginInfo(isAppEngine), systemDetails);
                    loginMainPanel.showPasswordReset(passwordResetToken);
                }

                @Override
                public void onSuccess(SystemDetails result) {
                    systemDetails = result;
                    isAppEngine = systemDetails.isGAE();
                    userService.loginRpc(GWT.getHostPageBaseURL(),
                            new LoginInfoAsyncCallback());
                }
            });


        } else if (doConnection(connectionRequestToken, email)) {

            userService.getSystemDetails(new AsyncCallback<SystemDetails>() {
                @Override
                public void onFailure(Throwable throwable) {
                    doConnectionApproval(userService, connectionRequestToken, email, isAppEngine);
                }

                @Override
                public void onSuccess(SystemDetails result) {
                    systemDetails = result;
                    isAppEngine = systemDetails.isGAE();
                    doConnectionApproval(userService, connectionRequestToken, email, isAppEngine);
                }
            });


        } else {
            userService.getSystemDetails(new AsyncCallback<SystemDetails>() {
                @Override
                public void onFailure(Throwable throwable) {
                    loadLoginView(UserModelFactory.createNullLoginInfo(isAppEngine), systemDetails);
                    loginMainPanel.showPasswordReset(passwordResetToken);
                }

                @Override
                public void onSuccess(SystemDetails result) {
                    systemDetails = result;
                    isAppEngine = systemDetails.isGAE();
                    loadLoginView(UserModelFactory.createNullLoginInfo(systemDetails.isGAE()), systemDetails);
                    loginMainPanel.showPasswordReset(passwordResetToken);
                }
            });

        }


    }

    private boolean doConnection(String connectionRequestToken, String email) {
        return connectionRequestToken != null && email != null;
    }

    private void doConnectionApproval(final UserServiceRpcAsync userService, final String token, final String email, final boolean isGAE) {


        try {

            userService.verifyEmail(email, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    FeedbackHelper.showError(throwable);
                }

                @Override
                public void onSuccess(Boolean connectionHasAccount) {
                    if (connectionHasAccount) {
                        try {

                            userService.processConnection(email, token,
                                    new AsyncCallback<Boolean>() {
                                        @Override
                                        public void onFailure(Throwable throwable) {
                                            FeedbackHelper.showError(throwable);
                                            connectionApproved = false;
                                        }

                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            isAppEngine = aBoolean;
                                            userService.loginRpc(GWT.getHostPageBaseURL(),
                                                    new LoginInfoAsyncCallback());
                                            connectionApproved = true;
                                        }
                                    });
                        } catch (UserServiceRpcException e) {
                            connectionApproved = false;
                            FeedbackHelper.showError(e);
                        }

                    } else {

                        loadLoginView(UserModelFactory.createNullLoginInfo(isGAE), systemDetails);
                        loginMainPanel.showRegisterDialog(email, token);
                    }
                }
            });


        } catch (Exception e) {
            connectionApproved = false;
            FeedbackHelper.showError(e);
        }
    }


    private void loadLoginView(final LoginInfo loginInfo, SystemDetails systemDetails) {


        Viewport viewport = new Viewport();

        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        //feedPanel.setLayout(new FitLayout());
        //feedPanel.setHeight("100%");

        loginMainPanel = new LoginMainPanel(this, loginInfo);


        ContentPanel center = new ContentPanel();
        //center.setHeadingText();
        center.setHeadingHtml("<a href=\"http://www.nimbits.com\">Nimbits</a> Console Login");
        center.setScrollMode(Style.Scroll.AUTOX);

        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 5, 0));


        center.add(loginMainPanel);


        viewport.add(center, centerData);


        viewport.setHeight(HEIGHT);
        RootPanel.get(MAIN).clear();
        RootPanel.get(MAIN).add(viewport);
        doLayout();


    }


    private void closeLoginWindows() {
        if (loginMainPanel != null) {
            loginMainPanel.hideWindows();
        }
    }

    @Override
    public void doGoogleLogin(LoginInfo loginInfo) {
        closeLoginWindows();
        Window.Location.replace(loginInfo.getLoginUrl());
    }

    @Override
    public void showLoginDialog(LoginInfo loginInfo) {
        closeLoginWindows();
        loadLoginView(loginInfo, systemDetails);
    }

    @Override
    public void doRegister() {
        closeLoginWindows();
        if (loginMainPanel != null) {
            loginMainPanel.showRegisterDialog(null, null);
        }
    }

    @Override
    public void loginSuccess(User user) {

        loadPortalView(user);

    }

    @Override
    public void onLogout() {
        closeLoginWindows();

        loadLoginView(UserModelFactory.createNullLoginInfo(isAppEngine), systemDetails);
    }

    @Override
    public void doForgot() {
        closeLoginWindows();
        if (loginMainPanel != null) {
            loginMainPanel.showForgotDialog();
        }
    }

    private void loadPortalView(final User user) {
        closeLoginWindows();

        Viewport viewport = new Viewport();

        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        //feedPanel.setLayout(new FitLayout());
        //feedPanel.setHeight("100%");

        CenterPanel centerPanel = new CenterPanel(user, this);


        ContentPanel center = new ContentPanel();

        center.setHeadingHtml("<a href=\"http://www.nimbits.com\">Nimbits</a>&nbsp;&nbsp;" + systemDetails.getVersion());

        center.setScrollMode(Style.Scroll.AUTOX);


        final ContentPanel east = new ContentPanel();
        // east.setHeading(Const.TEXT_DATA_FEED);
        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 5, 0));


        center.add(centerPanel);


        viewport.add(center, centerData);


        viewport.setHeight(HEIGHT);
        RootPanel.get(MAIN).clear();
        RootPanel.get(MAIN).add(viewport);
        doLayout();

    }

    private class LoginInfoAsyncCallback implements AsyncCallback<User> {


        LoginInfoAsyncCallback() {


        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
            closeLoginWindows();

            loadLoginView(UserModelFactory.createNullLoginInfo(isAppEngine), systemDetails);
        }

        @Override
        public void onSuccess(final User result) {
            LoginInfo loginInfo = result.getLoginInfo();

            closeLoginWindows();
            switch (loginInfo.getUserStatus()) {

                case newServer:
                    loadLoginView(result.getLoginInfo(), systemDetails);
                    break;
                case newUser:
                    loadPortalView(result);
                    break;

                case loggedIn:
                    loadPortalView(result);
                    break;
                case unknown:
                    loadLoginView(result.getLoginInfo(), systemDetails);
                    break;

            }
            if (connectionApproved) {
                FeedbackHelper.showInfo("Your connection request is now processed. You will now see your connection's data points and they will see yours. " +
                        "You can remove this connection by deleting it from the tree.");
            }


        }


    }


}
