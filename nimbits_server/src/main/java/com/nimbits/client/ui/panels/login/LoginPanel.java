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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.AbstractLoginPanel;


public class LoginPanel extends AbstractLoginPanel {

    private final UserServiceRpcAsync userService;


    private final FormPanel simple;
    private final Button submit = new Button("Login");

    private final LayoutContainer controlButtons = new LayoutContainer();
    private final Button register = new Button("Register");
    private final Button forgot = new Button("Forgot Password");
    private final boolean enableRegister;

    public LoginPanel(LoginListener loginListener, boolean enableRegister ) {
        super(loginListener);

        this.enableRegister = enableRegister;
        this.userService = GWT.create(UserServiceRpc.class);

        simple = new FormPanel();
        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);
        createForm();
    }

    protected void createForm() {

        final TextField<String> email = new TextField<String>();

        email.setFieldLabel("Email");
        email.setWidth(WIDTH);

        final TextField<String> password = new TextField<String>();

        password.setFieldLabel("Password");
        password.setWidth(WIDTH);
        password.setPassword(true);


        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(15));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        controlButtons.setLayout(layout);


        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));

        forgot.setWidth(BUTTON_WIDTH);
        register.setWidth(BUTTON_WIDTH);
        submit.setWidth(BUTTON_WIDTH);

        controlButtons.add(submit, layoutData);
        controlButtons.add(forgot, layoutData);
        controlButtons.add(register, layoutData);


        simple.add(email);
        simple.add(password);
        simple.add(controlButtons);

        simple.add(tosHtml);


        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                try {
                    userService.doLogin(email.getValue(), password.getValue(), new AsyncCallback<User>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            FeedbackHelper.showError(throwable);
                        }

                        @Override
                        public void onSuccess(User user) {
                            if (user != null) {
                                loginListener.loginSuccess(user);
                            }
                            else {
                                FeedbackHelper.showInfo("Invalid password or user not found");
                            }
                        }
                    });
                } catch (Throwable throwable) {
                    FeedbackHelper.showError(throwable);
                }


            }
        });

        register.setEnabled(enableRegister);
        register.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                loginListener.doRegister();

            }
        });

        forgot.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                loginListener.doForgot();
            }
        });

        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);


        vp.add(simple);

        add(vp);
        doLayout();
    }
}
