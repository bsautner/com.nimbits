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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.user.LoginInfo;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserStatus;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.AbstractLoginPanel;


public class LoginRegisterPanel extends AbstractLoginPanel {

    private final UserServiceRpcAsync userService = GWT.create(UserServiceRpc.class);


    private final FormPanel simple = new FormPanel();
    private LoginInfo loginInfo;

    private final LayoutContainer controlButtons = new LayoutContainer();
    private final Button register = new Button("Create Account");
    private final Button cancel = new Button("Cancel");

    private String recoveryToken;

    public LoginRegisterPanel(LoginListener loginListener, LoginInfo loginInfo) {
        super(loginListener, loginInfo);
        init(loginInfo);
        createForm();
    }

    private void init(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;


        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);
    }


    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
        emailField.setFieldLabel("Verify Email");
    }

    protected void createForm() {


        emailField.setWidth(WIDTH);
        emailField.setFieldLabel("Email");

        final TextField<String> password = new TextField<String>();

        password.setFieldLabel("Password");
        password.setWidth(WIDTH);
        password.setPassword(true);

        final TextField<String> password2 = new TextField<String>();

        password2.setFieldLabel("Verify Password");
        password2.setWidth(WIDTH);
        password2.setPassword(true);


        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(15));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        controlButtons.setLayout(layout);

        register.setWidth(BUTTON_WIDTH);
        cancel.setWidth(BUTTON_WIDTH);
        if (recoveryToken != null) {
            register.setText("Save Password");
        }
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        controlButtons.add(cancel, layoutData);
        controlButtons.add(register, layoutData);


        simple.add(emailField);
        simple.add(password);
        simple.add(password2);
        simple.add(controlButtons);
        if (loginInfo.isGAE()) {
            simple.add(googleLogin());
        }
        simple.add(tosHtml);
        if (loginInfo.getUserStatus().equals(UserStatus.newServer)) {
            Html helphtml = new Html("<br /><div><p><Strong>This appears to be a new Server Installation. " +
                    "The first user you register here will be configured as the " +
                    "system administrator. " +
                    "<Strong></p></div>");
            simple.add(helphtml);
        }
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                loginListener.showLoginDialog(loginInfo);
            }
        });


        register.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                String e = emailField.getValue();
                String p1 = password.getValue();
                String p2 = password2.getValue();
                if (e == null || e.trim().length() < 6 || !e.contains("@")) {
                    FeedbackHelper.showInfo("Please enter a valid email address");
                } else if (!p1.equals(p2)) {
                    FeedbackHelper.showInfo("Passwords do not match");
                } else {
                    try {
                        if (recoveryToken == null) {
                            userService.register(e, p1, getPendingConnectionToken(), new AsyncCallback<User>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    FeedbackHelper.showError(throwable);
                                }

                                @Override
                                public void onSuccess(User user) {
                                    loginListener.loginSuccess(user);
                                }
                            });
                        } else {
                            userService.resetPassword(e, p1, recoveryToken, new AsyncCallback<User>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    FeedbackHelper.showError(throwable);
                                }

                                @Override
                                public void onSuccess(User user) {
                                    loginListener.loginSuccess(user);
                                }
                            });

                        }
                    } catch (Throwable throwable) {
                        FeedbackHelper.showError(throwable);
                    }
                }


            }
        });


        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);


        vp.add(simple);

        add(vp);
        doLayout();
    }
}
