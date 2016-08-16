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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;
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



    @Override
    public void onModuleLoad() {

        final String passwordResetToken = Window.Location.getParameter(Parameters.rToken.getText());

        if (passwordResetToken == null) {
            loadLoginView();

        } else {
            loadLoginView( );
            loginMainPanel.showPasswordReset(passwordResetToken);
        }
    }

    private void loadLoginView() {


        Viewport viewport = new Viewport();

        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        loginMainPanel = new LoginMainPanel(this);


        ContentPanel center = new ContentPanel();
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
    public void showLoginDialog( ) {
        closeLoginWindows();
        loadLoginView( );
    }

    @Override
    public void doRegister() {
        closeLoginWindows();
        if (loginMainPanel != null) {
            loginMainPanel.showRegisterDialog(null);
        }
    }

    @Override
    public void loginSuccess(User user) {

        loadPortalView(user);

    }

    @Override
    public void onLogout() {
        closeLoginWindows();

        loadLoginView( );
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

        center.setHeadingHtml("<a href=\"http://www.nimbits.com\">Nimbits</a>&nbsp;&nbsp;" + Const.VERSION);

        center.setScrollMode(Style.Scroll.AUTOX);

        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 5, 0));


        center.add(centerPanel);


        viewport.add(center, centerData);


        viewport.setHeight(HEIGHT);
        RootPanel.get(MAIN).clear();
        RootPanel.get(MAIN).add(viewport);
        doLayout();

    }




}
