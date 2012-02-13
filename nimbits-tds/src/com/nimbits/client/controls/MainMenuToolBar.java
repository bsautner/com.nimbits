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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.*;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.panels.NavigationEventProvider;
import com.nimbits.client.service.instantmessage.*;
import com.nimbits.client.service.twitter.*;
import com.nimbits.client.service.user.*;
import com.nimbits.shared.*;

import java.util.*;


public class MainMenuToolBar extends NavigationEventProvider {
    private final UserServiceAsync userService = GWT.create(UserService.class);

    private final ContentPanel mainPanel = new ContentPanel();
    private int connectionCount = 0;
    private  LoginInfo loginInfo;



    private final UserServiceAsync us = GWT.create(UserService.class);

    public MainMenuToolBar(final String logoutURL,
                           final LoginInfo loginInfo,
                           final Map<String, String> settings) {
        // setLayout(new FlowLayout(0));

        final ToolBar toolBar = new ToolBar();
        this.loginInfo = loginInfo;
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

        toolBar.add(new SeparatorMenuItem());
        toolBar.add(connectionButton());
        toolBar.add(pendingConnectionsButton());

        toolBar.add(new SeparatorMenuItem());
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
    private Button connectionButton() {
        final Button b = new Button("New Connection");
        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addFriend()));

        b.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {


                final MessageBox box = MessageBox.prompt("Connect to Friends",
                        "Enter an email address to invite a friend to connect their Data Points to yours. After they approve your request " +
                                "you'll be able to see each others data points and diagrams (based on permission levels).");
                 box.addCallback(sendInviteLisenter());

            }
        });
        return b;
    }

    private Listener<MessageBoxEvent> sendInviteLisenter() {
        return new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
                final String email;
                email = be.getValue();
                if (email != null) {
                    if (email.length() > 0) {
                        UserServiceAsync userService;
                        userService = GWT.create(UserService.class);
                        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
                        userService.sendConnectionRequest(emailAddress, new AsyncCallback<Void>() {

                            @Override
                            public void onFailure(Throwable caught) {


                            }

                            @Override
                            public void onSuccess(Void result) {

                                Info.display("Connection Request", "Connection Request Sent!");

                            }

                        });


                    }
                }
            }

        };
    }

    private Button newKeyButton(final Listener<MessageBoxEvent> l) {
        Button SecretButton = new Button("Secret Key");
        SecretButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Key()));
        SecretButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
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
                    us.updateSecret(new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {


                        }

                        @Override
                        public void onSuccess(String key) {
                            Window.alert("Your new secret has been reset to: " + key + " and a copy has been emailed to you. Your old secret key is no longer valid.");

                        }

                    });

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
                IMService.sendInvite(new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {


                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.alert("Please check your instant messaging client for an invite to chat from nimbits1.appspot.com. You must accept the invitation in order for Nimbits to IM you.");

                    }

                });

            }
        });
        return IMButton;
    }
    private Button pendingConnectionsButton() {
        final Button connectionRequest = new Button("Connection Requests(" + connectionCount + ")");

        connectionRequest.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.add16()));
        userService.getPendingConnectionRequests(loginInfo.getEmailAddress(), new AsyncCallback<List<Connection>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);

            }

            @Override
            public void onSuccess(final List<Connection> result) {


                if (result.size() > 0) {
                final Menu scrollMenu = new Menu();
                scrollMenu.setMaxHeight(200);
                for (final Connection r : result) {
                    final MenuItem m = acceptConnectionMenuItem(scrollMenu, r);
                    scrollMenu.add(m);
                }


                connectionRequest.setMenu(scrollMenu);
                connectionCount = result.size();

                connectionRequest.setText("Requests(" + connectionCount + ")");
                }
                else {
                    connectionRequest.setVisible(false);
                }
                //	Window.alert("" + result.size());


            }

            private MenuItem acceptConnectionMenuItem(final Menu scrollMenu, final Connection r) {
                final MenuItem m = new MenuItem(r.getRequestorEmail().getValue());
                m.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connection()));
                m.addListener(Events.Select, new Listener<BaseEvent>() {

                    @Override
                    public void handleEvent(final BaseEvent be) {
                        //	final Dialog simple = new Dialog();
                        //simple.setHeading(");
                        final MessageBox box = new MessageBox();
                        box.setButtons(MessageBox.YESNOCANCEL);
                        box.setIcon(MessageBox.QUESTION);
                        box.setTitle("Connection request approval");
                        box.addCallback(new Listener<MessageBoxEvent>() {
                            @Override
                            public void handleEvent(final MessageBoxEvent be) {

                                final Button btn = be.getButtonClicked();

                                    if (btn.getText().equals("Yes")) {
                                        acceptConnection(r, true);
                                        scrollMenu.remove(m);
                                    } else if (btn.getText().equals("No")) {
                                        scrollMenu.remove(m);
                                        acceptConnection(r, false);
                                    }
                            }

                            private void acceptConnection(
                                    final Connection r,
                                    boolean accepted)  {
                                UserServiceAsync userService;
                                userService = GWT.create(UserService.class);
                                userService.connectionRequestReply(r.getTargetEmail(), r.getRequestorEmail(), r.getUUID(), accepted, new AsyncCallback<Void>() {

                                    @Override
                                    public void onFailure(Throwable e) {
                                        GWT.log(e.getMessage(), e);

                                    }

                                    @Override
                                    public void onSuccess(Void result) {

                                        connectionCount += (-1);

                                        connectionRequest.setText("Requests(" + connectionCount + ")");
                                        notifyReloadListener();

                                    }

                                });
                            }


                        });

                        box.setMessage("The owner of the email address: '" + r.getRequestorEmail().getValue() + "' would like to connect with you. You will have read only access to each others data points. Is that OK?");
                        box.show();


                    }

                });
                return m;
            }


        });
        return connectionRequest;
    }
}
