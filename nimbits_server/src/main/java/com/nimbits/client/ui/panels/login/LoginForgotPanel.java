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
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.AbstractLoginPanel;


public class LoginForgotPanel extends AbstractLoginPanel {

    private final UserServiceRpcAsync userService;


    private final FormPanel simple;

    private final Button cancel = new Button("Cancel");

    private final LayoutContainer controlButtons = new LayoutContainer();
    private final Button send = new Button("Send Email");

    public LoginForgotPanel(LoginListener loginListener) {
        super(loginListener);

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


        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(15));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        controlButtons.setLayout(layout);

        send.setWidth(BUTTON_WIDTH);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        controlButtons.add(send, layoutData);
        controlButtons.add(cancel, layoutData);

        simple.add(email);

        simple.add(controlButtons);

        simple.add(tosHtml);

        Html html;


        html = new Html("<br><p>Enter your email.  The system will send you instructions to reset your password. </p>" +
                "<br>" +
                "<p>If this is a private server, you'll need to ensure that: </p><br>" +

                "<ul><li> This server is configured to send email over smtp</li>" +
                "<li> That the system admin setup smtp properly - setting the smtp password and server url in the " +
                "settings menu</li>" +
                "<li> That the email you registered one was valid and can receive email</li>" +
                "</ul>" +
                "<p><a href=\"http://www.nimbits.com/howto_server_mail.jsp\">Learn More</a></p>")
        ;


        simple.add(html);

        send.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                try {
                    userService.doForgotPassword(email.getValue(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            FeedbackHelper.showError(throwable);
                        }

                        @Override
                        public void onSuccess(Void user) {
                            FeedbackHelper.showInfo("An email has been sent with instructions to reset your password");
                        }
                    });
                } catch (Throwable throwable) {
                    FeedbackHelper.showError(throwable);
                }


            }
        });

        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                loginListener.onLogout();

            }
        });


        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);


        vp.add(simple);

        add(vp);
        doLayout();
    }
}
