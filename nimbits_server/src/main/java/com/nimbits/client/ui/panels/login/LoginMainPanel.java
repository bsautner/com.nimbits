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

package com.nimbits.client.ui.panels.login;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.nimbits.client.model.user.LoginInfo;


public class LoginMainPanel extends LayoutContainer {

    protected static final int SIZE = 400;
    private Window window;

    private LoginInfo loginInfo;
    private final LoginListener loginListener;


    public LoginMainPanel(LoginListener loginListener, final LoginInfo user) {
        this.loginInfo = user;
        this.loginListener = loginListener;

    }

    @Override
    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        loadLayout();
    }


    private void loadLayout() {

        final ContentPanel panel = new ContentPanel();
        if (window != null) {
            window.hide();
        }

        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(false);

        panel.setFrame(false);
        panel.setBodyBorder(true);
        panel.setCollapsible(true);


        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(0));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);

        add(panel, new FlowData(0));

        layout(true);


        switch (loginInfo.getUserStatus()) {

            case newServer:
                showRegisterDialog(null);
            case newUser:
                break;
            case loggedIn:
                break;
            case unknown:
                showLoginDialog();
        }


    }

    private void showLoginDialog() {
        LoginPanel dp = new LoginPanel(loginListener, loginInfo);
        window = new com.extjs.gxt.ui.client.widget.Window();
        window.setWidth(SIZE);
        window.setHeight(SIZE);
        window.setHeadingText("Login");
        window.setClosable(false);
        window.setBlinkModal(true);
        window.add(dp);


        window.show();
    }

    public void hideWindows() {
        if (window != null) {
            window.hide();
        }
    }


    public void showRegisterDialog(String email) {
        hideWindows();
        LoginRegisterPanel dp = new LoginRegisterPanel(loginListener, loginInfo);

        dp.setEmail(email);
        window = new com.extjs.gxt.ui.client.widget.Window();
        window.setWidth(SIZE);
        window.setHeight(SIZE);
        window.setClosable(false);
        window.setHeadingText("Register New Account");
        window.add(dp);


        window.show();
    }


    public void showForgotDialog() {

        LoginForgotPanel dp = new LoginForgotPanel(loginListener, loginInfo);
        window = new com.extjs.gxt.ui.client.widget.Window();
        window.setWidth(SIZE);
        window.setHeight(SIZE);
        window.setClosable(false);
        window.setHeadingText("Forgot Password");
        window.add(dp);


        window.show();
    }

    public void showPasswordReset(String p) {
        hideWindows();
        LoginRegisterPanel dp = new LoginRegisterPanel(loginListener, loginInfo);
        dp.setRecoveryToken(p);
        window = new com.extjs.gxt.ui.client.widget.Window();
        window.setWidth(SIZE);
        window.setHeight(SIZE);
        window.setClosable(false);
        window.setHeadingText("Reset Password");
        window.add(dp);


        window.show();
    }


}
