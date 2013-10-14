/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.ui.panels;

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
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.GxtModel;
import com.nimbits.cloudplatform.client.model.TreeModel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.entity.EntityServiceRpc;
import com.nimbits.cloudplatform.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.cloudplatform.client.service.xmpp.XMPPService;
import com.nimbits.cloudplatform.client.service.xmpp.XMPPServiceAsync;
import com.nimbits.cloudplatform.client.ui.controls.MainMenuBar;
import com.nimbits.cloudplatform.client.ui.helper.FeedbackHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/23/11
 * Time: 10:37 AM
 */
public class CenterPanel extends NavigationEventProvider {


    private static final double HEIGHT1 = 1;
    private NavigationPanel navigationPanel;
    private Map<String, String> settings;

    private final User user;

    public CenterPanel(final User user, final Map<String, String> settings) {
        this.user = user;
        this.settings = settings;
    }

    @Override
    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        loadLayout();
  }

    private NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree = new NavigationPanel(user, settings);


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
        navigationPanel.setHeight(Window.getClientHeight());
        panel.add(navigationPanel);

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(0));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);

        add(panel, new FlowData(0));
        //chartPanel.layout();
        layout(true);

    }

    public void addEntityToTree(TreeModel model) {
        navigationPanel.addUpdateTreeModel(model, false);
    }

    private MainMenuBar initToolbar(final User loginInfo) {
        MainMenuBar toolBar = new MainMenuBar(loginInfo);
        toolBar.addEntityModifiedListeners(new MainMenuBar.EntityModifiedListener() {
            @Override
            public void onEntityModified(TreeModel model, Action action) {
                switch (action) {
                    case update:
                    case create:
                        navigationPanel.addUpdateTreeModel(model, false);
                        break;
                    case refresh:
                        navigationPanel.getUserEntities(true);
                }
            }
        });
        toolBar.addActionListeners(new ActionListener(loginInfo));
        return toolBar;
    }

    private static void sendXMPPInvite() {
        XMPPServiceAsync IMService = GWT.create(XMPPService.class);
        IMService.sendInviteRpc(new XMPPInviteAsyncCallback());
    }

    public void addEntity(final TreeModel model) {
        //  try {
        switch (model.getEntityType()) {
            case user:
                break;
            case point:
                break;
            case subscription:
                displaySubscription(model.getBaseEntity());
                break;

        }
//        } catch (Exception ex) {
//            FeedbackHelper.showError(ex);
//        }

    }

    private void displaySubscription(final Entity entity) {
        EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
        service.getEntityByKeyRpc(user, entity.getKey(), EntityType.subscription, new GetSubscribedEntityAsyncCallback());
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

    private class GetSubscribedEntityAsyncCallback implements AsyncCallback<List<Entity>> {

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(List<Entity> result) {
            try {
                if (!result.isEmpty()) {
                    addEntity(new GxtModel(result.get(0)));
                }

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
        private final User loginInfo;

        public ActionListener(User loginInfo) {
            this.loginInfo = loginInfo;
        }

        @Override
        public void onAction(Action action) {
            switch (action) {
                case expand:
                    navigationPanel.toggleExpansion();
                    break;
                case logout:
                    final String logoutUrl = loginInfo != null ? loginInfo.getLogoutUrl() : Path.PATH_NIMBITS_HOME;
                    Window.Location.replace(logoutUrl);
                    break;
                case xmpp:
                    sendXMPPInvite();
                    break;
                case save:
                    navigationPanel.saveAll();
                    break;
            }
        }
    }
}
