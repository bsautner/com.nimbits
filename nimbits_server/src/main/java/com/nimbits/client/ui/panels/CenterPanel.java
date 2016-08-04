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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcAsync;
import com.nimbits.client.service.xmpp.XmppRpcService;
import com.nimbits.client.service.xmpp.XmppRpcServiceAsync;
import com.nimbits.client.ui.controls.MainMenuBar;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.panels.login.LoginListener;


public class CenterPanel extends NavigationEventProvider implements BasePanel.PanelEvent {
    private static final String PATH_NIMBITS_HOME = "http://www.nimbits.com";

    private NavigationPanel navigationPanel;
    private com.extjs.gxt.ui.client.widget.Window w;

    private final User user;
    private final LoginListener loginListener;

    public CenterPanel(final User user, LoginListener loginListener) {
        this.user = user;
        this.loginListener = loginListener;

    }

    @Override
    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        loadLayout();
    }

    private NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree = new NavigationPanel(user);


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

        MainMenuBar toolBar = initToolbar(user);
        panel.setTopComponent(toolBar);
        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(false);

        panel.setFrame(false);
        panel.setBodyBorder(true);
        panel.setCollapsible(true);

        navigationPanel = createNavigationPanel();
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(Window.getClientHeight() - 100);
        panel.add(navigationPanel);

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(0));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);

        add(panel, new FlowData(0));
        //chartPanel.layout();
        layout(true);

    }


    private MainMenuBar initToolbar(final User loginInfo) {
        MainMenuBar toolBar = new MainMenuBar(loginInfo);

        toolBar.addActionListeners(new ActionListener(loginInfo, this));
        return toolBar;
    }

    private static void sendXMPPInvite() {
        XmppRpcServiceAsync IMService = GWT.create(XmppRpcService.class);
        IMService.sendInviteRpc(new XMPPInviteAsyncCallback());
    }

    public void addEntity(final TreeModel model) {

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
        w.hide();
    }

    private static class XMPPInviteAsyncCallback implements AsyncCallback<Void> {

        XMPPInviteAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {

            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Void result) {
            FeedbackHelper.showInfo("Please check your instant messaging client for an invite to chat from nimbits1.appspot.com. You must accept the invitation in order for Nimbits to IM you.");

        }

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

    private class ActionListener implements MainMenuBar.ActionListener {
        private final User user;
        private BasePanel.PanelEvent listener;

        public ActionListener(User user, BasePanel.PanelEvent panelEvent) {
            this.user = user;
            this.listener = panelEvent;
        }

        @Override
        public void onAction(Action action) throws ValueException {
            switch (action) {
                case expand:
                    navigationPanel.toggleExpansion();
                    break;
                case rest:
                    Window.Location.replace("/service/v3/rest/me");
                    break;
                case logout:
                    final String logoutUrl = user != null ? user.getLoginInfo().getLogoutUrl() : PATH_NIMBITS_HOME;

                    UserServiceRpcAsync userService = GWT.create(UserServiceRpc.class);
                    userService.logout(new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable throwable) {
                            FeedbackHelper.showError(throwable);
                        }

                        @Override
                        public void onSuccess(Void aVoid) {

                            if (user != null && user.getSource().equals(UserSource.google)) {

                                Window.Location.replace(logoutUrl);
                            } else {
                                loginListener.onLogout();

                            }

                        }
                    });

                    break;
                case xmpp:
                    sendXMPPInvite();
                    break;
                case save:
                    navigationPanel.saveAll();
                    break;
                case admin:
                    SettingPanel dp = new SettingPanel(listener);
                    w = new com.extjs.gxt.ui.client.widget.Window();
                    w.setWidth(BasePanel.WIDTH);
                    w.setHeight(BasePanel.HEIGHT);
                    w.setHeadingText("Edit Server Settings");
                    w.add(dp);


                    w.show();
                    break;


            }
        }
    }
}
