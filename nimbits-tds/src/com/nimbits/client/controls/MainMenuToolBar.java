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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.service.instantmessage.IMService;
import com.nimbits.client.service.instantmessage.IMServiceAsync;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.client.service.twitter.TwitterServiceAsync;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.shared.Utils;

import java.util.Map;

import static com.google.gwt.user.client.Window.alert;


public class MainMenuToolBar extends LayoutContainer {
    //    private static final Icons.INSTANCE. Icons.INSTANCE. = GWT.create(Icons.INSTANCE.class);
    private final UserServiceAsync us = GWT.create(UserService.class);

    public MainMenuToolBar(final String logoutURL, final LoginInfo loginInfo, final Map<String, String> settings) {
        // setLayout(new FlowLayout(0));

        final ToolBar toolBar = new ToolBar();

        toolBar.setBorders(true);

        toolBar.add(new SeparatorToolItem());

        final Button logoutButton = new Button("Logout");

        final Button IMButton = instantMessageButton();

        final Button TwitterButton = twitterButton(loginInfo.getEmailAddress());

        final Button FBButton = facebookButton();

        final Listener<MessageBoxEvent> l = newKeyListener();

        final Button secretButton = newKeyButton(l);

        logoutButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Restart()));
        logoutButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(final BaseEvent be) {
                Window.Location.replace(logoutURL);

            }
        });


        final Button homeButton = new Button("nimbits.com");
        homeButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Home()));
        homeButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                Window.open("http://www.nimbits.com", "", "");

            }
        });

        final Button helpButton = new Button("Help");
        helpButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Help()));
        helpButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                Window.open("http://groups.google.com/group/nimbits", "", "");
            }
        });

//        final Button adminButton = new Button("Admin");
//        adminButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Key()));
//        adminButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
//
//            @Override
//            public void handleEvent(BaseEvent be) {
//                alert("This is where the admin feature is going");
//                // Window.open("http://groups.google.com/group/nimbits", "", "");
//
//            }
//        });

        toolBar.add(helpButton);
        toolBar.add(homeButton);

        toolBar.add(IMButton);


        if (settings.containsKey(Const.SETTING_TWITTER_CLIENT_ID) && !Utils.isEmptyString(settings.get(Const.SETTING_TWITTER_CLIENT_ID)) && loginInfo != null) {
            toolBar.add(TwitterButton);
        }
        if (settings.containsKey(Const.SETTING_FACEBOOK_API_KEY) && !Utils.isEmptyString(settings.get(Const.SETTING_FACEBOOK_API_KEY))) {
            toolBar.add(FBButton);
        }

        toolBar.add(secretButton);

        toolBar.add(logoutButton);
        toolBar.add(new SeparatorMenuItem());

        if (loginInfo.isUserAdmin()) {
            //toolBar.add(adminButton);
        }

        ContentPanel panel = new ContentPanel();
        panel.setCollapsible(false);

        panel.setHeaderVisible(false);
        panel.setFrame(false);

        panel.setTopComponent(toolBar);

        LayoutContainer c = new LayoutContainer();
        c.setStyleAttribute("backgroundColor", "white");
        c.setBorders(true);
        panel.add(c);
        // ContentPanel p = new ContentPanel();

        //  p.setUrl(Const.PATH_WA_URL);
        //   add(p);
        //Html h = new Html()
        add(toolBar);
    }


    private Button newKeyButton(final Listener<MessageBoxEvent> l) {
        Button SecretButton = new Button("Secret Key");
        SecretButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Key()));
        SecretButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                try {
                    us.getSecret(new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        @Override
                        public void onSuccess(String s) {
                            MessageBox.confirm("Reset Your Key",
                                    "Your secret Key is currently set to: " + s +
                                            "<br> Press YES to generate a new secret key and to have it emailed to the account you are currently logged in with. " +
                                            "Your old key will no longer be valid. You can use your key to use Nimbits web services.",
                                    l);

                        }
                    });
                } catch (NimbitsException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        });
        return SecretButton;
    }

    private Listener<MessageBoxEvent> newKeyListener() {
        return new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent ce) {
                Button btn = ce.getButtonClicked();
                if (btn.getText().toLowerCase().equals("yes")) {
                    try {
                        us.updateSecret(new AsyncCallback<String>() {

                            @Override
                            public void onFailure(Throwable caught) {


                            }

                            @Override
                            public void onSuccess(String key) {
                                Window.alert("Your new secret has been reset to: " + key + " and a copy has been emailed to you. Your old secret key is no longer valid.");

                            }

                        });
                    } catch (NimbitsException e) {
                        alert(e.getMessage());
                    }

                }

            }
        };
    }

    private Button facebookButton() {
        Button FBButton = new Button("Enable facebook");
        FBButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.text()));
        FBButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                Window.Location.replace("http://apps.facebook.com/Nimbits");

            }
        });
        return FBButton;
    }

    private Button twitterButton(final EmailAddress email) {
        Button TwitterButton = new Button("Enable Twitter");
        TwitterButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.text()));
        TwitterButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {


                TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
                try {
                    twitterService.twitterAuthorise(email, new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            GWT.log(caught.getMessage(), caught);
                        }

                        @Override
                        public void onSuccess(String result) {
                            //	Window.alert(result);
                            Window.Location.replace(result);

                        }

                    });
                } catch (NimbitsException e) {
                    Window.alert("This server may not be configured for twitter");

                }


                //    Window.open("http://www.nimbits.com?TW=1&email=" + email, "", "");


            }
        });
        return TwitterButton;
    }

    private Button instantMessageButton() {
        Button IMButton = new Button("Enable Instant Messaging");
        IMButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.list_items()));

        IMButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {

                IMServiceAsync IMService = GWT.create(IMService.class);
                try {
                    IMService.sendInvite(new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {


                        }

                        @Override
                        public void onSuccess(Void result) {
                            Window.alert("Please check your instant messaging client for an invite to chat from nimbits1.appspot.com. You must accept the invitation in order for Nimbits to IM you.");

                        }

                    });
                } catch (NimbitsException ignored) {

                }

            }
        });
        return IMButton;
    }

}
