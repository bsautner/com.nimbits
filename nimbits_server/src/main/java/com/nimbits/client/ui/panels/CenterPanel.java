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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.login.LoginListener;


public class CenterPanel extends NavigationEventProvider implements BasePanel.PanelEvent {

    private final User user;
    private int refreshRate;
    private static final String SESSION = "session";
    private static final String EMAIL = "email";
    private final LoginListener loginListener;

    public CenterPanel(LoginListener loginListener, final User user, int refreshRate ) {
        this.user = user;
        this.refreshRate = refreshRate;
        this.loginListener = loginListener;

    }

    @Override
    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        loadLayout();
    }

    private NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree = new NavigationPanel(user, refreshRate);


        navTree.addEntityClickedListeners(new AddEntityEntityClickedListener());

        navTree.addEntityDeletedListeners(new EntityDeletedListener() {

            @Override
            public void onEntityDeleted(final Entity c) {
                notifyEntityDeletedListener(c);

            }

        });


        return navTree;

    }

    private void loadLayout() {

        final ContentPanel panel = new ContentPanel();

        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(false);

        panel.setFrame(false);
        panel.setBodyBorder(false);
        panel.setCollapsible(false);


        Command cmd = new Command() {
            public void execute() {
                Window.alert("You selected a menu item!");
            }
        };

        Command logoutCommand = new Command() {
            public void execute() {


                UserServiceRpcAsync service = GWT.create(UserServiceRpc.class);
                service.logout(Cookies.getCookie(SESSION), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        FeedbackHelper.showError(throwable);
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        Cookies.removeCookie(SESSION);
                        Cookies.removeCookie(EMAIL);
                        loginListener.onLogout();

                    }
                });

            }
        };

        // Make some sub-menus that we will cascade from the top menu.
        MenuBar optionsMenu = new MenuBar(true);

        optionsMenu.addItem("Logout", logoutCommand);
      //  fooMenu.addItem("foo", cmd);
      //  fooMenu.addItem("menu", cmd);

        MenuBar barMenu = new MenuBar(true);
        barMenu.addItem("the", cmd);
        barMenu.addItem("bar", cmd);
        barMenu.addItem("menu", cmd);

        MenuBar bazMenu = new MenuBar(true);
        bazMenu.addItem("the", cmd);
        bazMenu.addItem("baz", cmd);
        bazMenu.addItem("menu", cmd);

        // Make a new menu bar, adding a few cascading menus to it.
        MenuBar menu = new MenuBar();
       // menu.addItem("Options", optionsMenu);
      //  menu.addItem("bar", barMenu);
        //menu.addItem("baz", bazMenu);

        // Add it to the root panel.
       // RootPanel.get().add(menu);


        panel.add(menu);


        NavigationPanel navigationPanel = createNavigationPanel();
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(Window.getClientHeight());
        panel.add(navigationPanel);

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(0));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);

        add(panel, new FlowData(0));
        //chartPanel.layout();
        layout(true);

    }



    private void addEntity(final TreeModel model) {

        switch (model.getEntityType()) {
            case user:
                break;
            case point:
                break;
            case subscription:
                displaySubscription(model.getBaseEntity());
                break;

        }

    }

    private void displaySubscription(final Entity entity) {
        EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
        service.getEntityByKeyRpc(user, entity.getId(), EntityType.subscription, new GetSubscribedEntityAsyncCallback());
    }

    @Override
    public void close() {

    }


    private class GetSubscribedEntityAsyncCallback implements AsyncCallback<Entity> {

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(Entity result) {
            try {

                addEntity(new GxtModel(result));


            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class AddEntityEntityClickedListener implements EntityClickedListener {

        AddEntityEntityClickedListener() {
        }

        @Override
        public void onEntityClicked(final TreeModel c) {
            addEntity(c);

        }

    }


    }



