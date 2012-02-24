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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.controls.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.instantmessage.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.client.service.twitter.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/23/11
 * Time: 10:37 AM
 */
public class CenterPanel extends NavigationEventProvider {

    final private Map<String, Entity> entities = new HashMap<String, Entity>();
    //final private PointGridPanel grid = new PointGridPanel();
    private final Map<String, AnnotatedTimeLinePanel> lines = new HashMap<String, AnnotatedTimeLinePanel>();
    private ContentPanel bottom;
    private NavigationPanel navigationPanel;
    private Map<String, String> settings;
    private LoginInfo loginInfo;


    public CenterPanel(LoginInfo info, Map<String, String> settings) {
        this.loginInfo = info;
        this.settings = settings;
    }

    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
//        grid.addEntityClickedListeners(new EntityClickedListener() {
//
//            @Override
//            public void onEntityClicked(final GxtModel entity) {
//
//                addEntity(entity);
//
//
//            }
//        });
//
//        grid.addValueEnteredListeners(new ValueEnteredListener() {
//
//            @Override
//            public void onValueEntered(final Entity entity, final Value value) {
//                for (AnnotatedTimeLinePanel line : lines.values()) {
//                    if (line.containsPoint(entity)) {
//                        line.addValue(entity, value);
//                    }
//                }
//            }
//        });


        loadLayout();

    }



    private AnnotatedTimeLinePanel createLine(final String name) {
        final AnnotatedTimeLinePanel line = new AnnotatedTimeLinePanel(true, name);
        line.setSelected(true);
        line.addChartRemovedClickedListeners(new ChartRemovedListener() {
            @Override
            public void onChartRemovedClicked(String chartName) {
                lines.remove(chartName);

                addLinesToBottom();

            }
        });
        line.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent baseEvent) {
                for (AnnotatedTimeLinePanel l : lines.values()) {
                    l.setSelected(false);
                }
                line.setSelected(true);
            }
        });
        line.isSelected();
        for (AnnotatedTimeLinePanel l : lines.values()) {
            l.setSelected(false);
        }

        return line;
    }

    private boolean isOneLineSelected() {
        for (AnnotatedTimeLinePanel l : lines.values()) {
            if (l.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void addLinesToBottom() {
        initBottomPanel();
        assert (lines.size() > 0);
        if (!isOneLineSelected() && lines.values().iterator().hasNext()) {
            lines.values().iterator().next().setSelected(true);
        }
        for (final AnnotatedTimeLinePanel l : lines.values()) {
            if (lines.size() == 1) {
                l.setSelected(true);
            }
            double w = 1.0 / (double) lines.size();
            l.initChart();
            //   l.refreshChart();
            bottom.add(l, new RowData(w, 1, new Margins(4)));
        }
        add(bottom, new FlowData(0));
        doLayout(true);
    }

    private void initBottomPanel() {
        if (bottom != null && getItems().contains(bottom)) {
            remove(bottom);
        }

        bottom = bottomPanel();
    }



    final NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree =
                new NavigationPanel(loginInfo.getUser(), settings);


        navTree.addEntityClickedListeners(new EntityClickedListener() {

            @Override
            public void onEntityClicked(final GxtModel c)  {
                //  center.addEntity(c);
                //notifyEntityClickedListener(c);

            }

        });

        navTree.addEntityDeletedListeners(new EntityDeletedListener() {

            @Override
            public void onEntityDeleted(final Entity c)  {
                notifyEntityDeletedListener(c);
                //TODO center.removePoint(c);
            }

        });


        return navTree;

    }

    private void loadLayout() {

        final ContentPanel panel = new ContentPanel(new FillLayout());
        final String logoutUrl = (loginInfo != null) ? loginInfo.getLogoutUrl() : Const.PATH_NIMBITS_HOME;

        MainMenuBar toolBar = initToolbar(loginInfo, settings, logoutUrl);
        panel.setTopComponent(toolBar);
        // panel.setHeading("Data Channels");
        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(true);
        panel.setHeading(Const.CONST_SERVER_NAME + " " + Const.CONST_SERVER_VERSION);
        panel.setFrame(true);
        panel.setCollapsible(true);
        // panel.setHeight("100%");

        navigationPanel = createNavigationPanel();
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(400);
        panel.add(navigationPanel, new RowData(1, 1, new Margins(4)));
        // panel.setTopComponent(toolbar());
        add(panel, new FlowData(0));

        //  final private AnnotatedTimeLinePanel line1 = new AnnotatedTimeLinePanel(true);
        addBlankLineToBottom();

        // add(bottom, new FlowData(0));
        layout(true);

    }

    private MainMenuBar initToolbar(final LoginInfo loginInfo, Map<String, String> settings, String logoutUrl) {
        MainMenuBar toolBar = new MainMenuBar(loginInfo, settings);
        toolBar.addEntityModifiedListeners(new MainMenuBar.EntityModifiedListener() {
            @Override
            public void onEntityModified(GxtModel model, Action action) {
                switch (action) {
                    case update: case create:
                        navigationPanel.addUpdateTreeModel(model, false);
                        break;
                    case refresh:
                        navigationPanel.getUserEntities(true);
                }


            }
        } );
        toolBar.addActionListeners(new MainMenuBar.ActionListener() {
            @Override
            public void onAction(Action action) {
                switch (action) {
                    case expand:
                        navigationPanel.toggleExpansion();
                        break;
                    case logout:
                        Window.Location.replace(loginInfo.getLogoutUrl());
                        break;
                    case xmpp:
                        sendXMPPInvite();
                        break;
                    case facebook:
                        Window.Location.replace("http://apps.facebook.com/Nimbits");
                        break;
                    case twitter:
                        twitterAuthorise();
                        break;
                    case addChart:
                        final String name = (lines.size() == 0) ? Const.DEFAULT_CHART_NAME : "line" + lines.size() + 1;
                        lines.put(name, createLine(name));
                        addLinesToBottom();
                        break;
                    case save:
                        navigationPanel.saveAll();
                        break;
                }
            }
        });
        return toolBar;
    }

    private void sendXMPPInvite() {
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

    private void twitterAuthorise() {
        TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        twitterService.twitterAuthorise(loginInfo.getEmailAddress(), new AsyncCallback<String>() {

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
    }

    private void addBlankLineToBottom() {
        final AnnotatedTimeLinePanel line = createLine(Const.DEFAULT_CHART_NAME);
        lines.put(Const.DEFAULT_CHART_NAME, line);
        line.setHeight(400);
        addLinesToBottom();
    }

    private ContentPanel bottomPanel() {
        final ContentPanel bottom = new ContentPanel();

        bottom.setLayout(new RowLayout(Style.Orientation.HORIZONTAL));
        bottom.setFrame(false);
        bottom.setCollapsible(true);
        bottom.setHeaderVisible(false);
        bottom.setHeight(400);
        return bottom;
    }



    public void addEntity(final GxtModel entity) {

//        for (final Entity e : entity.getChildren()) {
//            addEntity(e);
//        }

        switch (entity.getEntityType()) {
            case user:
                break;
            case point:
                displayPoint(entity.getBaseEntity());
                break;
            case category:
                break;
            case file:
                showFile(entity.getBaseEntity());
                break;
            case subscription:
                displaySubscription(entity.getBaseEntity());
                break;
            case userConnection:
                break;
        }

    }

    private void showFile(Entity entity) {
        final String resourceUrl =
                Const.PATH_BLOB_SERVICE +
                        "?" + Const.PARAM_BLOB_KEY + "=" + entity.getBlobKey();
        Window.open(resourceUrl, entity.getName().getValue(), "");
    }

    private void displaySubscription(final Entity entity) {
        SubscriptionServiceAsync service = GWT.create(SubscriptionService.class);
        service.getSubscribedEntity(entity, new AsyncCallback<Entity>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(Entity result) {
                addEntity( new GxtModel(result));
            }
        });

    }

    private void displayPoint(final Entity entity) {
        if (!entities.containsKey(entity.getEntity())) {
            entities.put(entity.getEntity(), entity);

        }

        for (final AnnotatedTimeLinePanel line : lines.values()) {
            if (!line.containsPoint(entity) && line.isSelected()) {
                PointServiceAsync service = GWT.create(PointService.class);
                line.addPoint(entity);
                //id.addEntity(entity);

            }
        }


    }

}
