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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;

import java.util.List;

class ConnectionPanel extends NavigationEventProvider {


    private final UserServiceAsync userService = GWT.create(UserService.class);
    private final EmailAddress email;
    private final ContentPanel mainPanel = new ContentPanel();
    private int connectionCount = 0;

//    @Override
//    protected void onRender(com.google.gwt.user.client.Element parent, int pos) {
//        super.onRender(parent, pos);
//    }

    private UserListPanel createUserList() {
        final UserListPanel userList = new UserListPanel(email);
        userList.addCategoryClickedListeners(new CategoryClickedListener() {

            @Override
            public void onCategoryClicked(final Category c, boolean readOnly) {
                notifyCategoryClickedListener(c, readOnly);
            }
        });

        userList.addPointClickedListeners(new PointClickedListener() {
            @Override
            public void onPointClicked(Point p){
                notifyPointClickedListener(p);
            }
        });

        userList.addDiagramClickedListeners(new DiagramClickedListener() {

            @Override
            public void onDiagramClicked(final Diagram p) {

                notifyDiagramClickedListener(p);
            }

        });

        return userList;

    }

    public ConnectionPanel(final EmailAddress email)  {
        this.email = email;

        mainPanel.setTopComponent(toolbar());
        mainPanel.setHeaderVisible(false);
        mainPanel.add(createUserList());
        mainPanel.setFrame(false);


        add(mainPanel);

//			Image i = new Image();
//			i.setWidth("170px");
//			i.setHeight("140px");
//			i.setUrl("resources/images/net.jpg");
//			i.setStyleName("CenterImage");
        //	 add(i);

    }

    private ToolBar toolbar()  {
        final ToolBar t = new ToolBar();
        t.setBorders(false);
        ButtonGroup group = new ButtonGroup(1);
        group.setBodyBorder(false);
        group.setBorders(false);
        //group.setFrame(false);
        group.setWidth(getWidth());

        Button connectionRequest = pendingConnectionsButton();
        Button FriendButton = connectionButton();
        Button refreshButton = showConnectionsButton();
        group.add(FriendButton);
        group.add(connectionRequest);
        group.add(refreshButton);
        t.add(group);


        return t;

    }

    private Button showConnectionsButton() {
        final Button b = new Button("Show Connections");
        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh()));

        b.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                reloadConnections();


            }


        });
        return b;

    }

    private void reloadConnections() {
        mainPanel.removeAll();
        mainPanel.add(createUserList());
        doLayout(true);
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
                box.addCallback(new Listener<MessageBoxEvent>() {
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

                });

            }
        });
        return b;
    }

    private Button pendingConnectionsButton() {
        final Button connectionRequest = new Button("Requests(" + connectionCount + ")");

        connectionRequest.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.add16()));
        userService.getPendingConnectionRequests(email, new AsyncCallback<List<Connection>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);

            }

            @Override
            public void onSuccess(final List<Connection> result) {


                final Menu scrollMenu = new Menu();
                scrollMenu.setMaxHeight(200);
                for (final Connection r : result) {
                    final MenuItem m = new MenuItem(r.getRequestorEmail().getValue());
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
                                    try {
                                        if (btn.getText().equals("Yes")) {

                                            acceptConnection(r, true);

                                            scrollMenu.remove(m);
                                        } else if (btn.getText().equals("No")) {
                                            scrollMenu.remove(m);

                                            acceptConnection(r, false);

                                        }
                                    } catch (NimbitsException e) {
                                        GWT.log(e.getMessage(), e);
                                    }

                                }

                                private void acceptConnection(
                                        final Connection r,
                                        boolean accepted) throws NimbitsException {
                                    UserServiceAsync userService;
                                    userService = GWT.create(UserService.class);
                                    userService.connectionRequestReply(r.getTargetEmail(), r.getRequestorEmail(), r.getUUID(), accepted, new AsyncCallback<Void>() {

                                        @Override
                                        public void onFailure(Throwable e) {
                                            GWT.log(e.getMessage(), e);

                                        }

                                        @Override
                                        public void onSuccess(Void result) {
                                            reloadConnections();
                                            connectionCount += (-1);

                                            connectionRequest.setText("Requests(" + connectionCount + ")");

                                        }

                                    });
                                }


                            });

                            box.setMessage("The owner of the email address: '" + r.getRequestorEmail().getValue() + "' would like to connect with you. You will have read only access to each others data points. Is that OK?");
                            box.show();


                        }

                    });
                    scrollMenu.add(m);
                }


                connectionRequest.setMenu(scrollMenu);
                connectionCount = result.size();

                connectionRequest.setText("Requests(" + connectionCount + ")");

                //	Window.alert("" + result.size());


            }


        });
        return connectionRequest;
    }
}
