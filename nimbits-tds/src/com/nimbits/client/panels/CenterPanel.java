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
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.controls.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
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

    // private final Map<String, Entity> entities = new HashMap<String, Entity>();
    // private final Map<String, AnnotatedTimeLinePanel> lines = new HashMap<String, AnnotatedTimeLinePanel>();

    private NavigationPanel navigationPanel;
    private Map<String, String> settings;
    private LoginInfo loginInfo;
    private LayoutContainer chartContainer;
    private int chartHeight;
    private HBoxLayoutData flex;

    public CenterPanel(LoginInfo info, Map<String, String> settings) {
        this.loginInfo = info;
        this.settings = settings;
    }

    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        loadLayout();

    }

    final NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree =
                new NavigationPanel(loginInfo.getUser(), settings);


        navTree.addEntityClickedListeners(new EntityClickedListener() {

            @Override
            public void onEntityClicked(final GxtModel c)  {
                addEntity(c);

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

        final ContentPanel panel = new ContentPanel( );
        chartHeight  = Double.valueOf((Window.getClientHeight() * .9)  / 2).intValue();

        MainMenuBar toolBar = initToolbar(loginInfo, settings);
        panel.setTopComponent(toolBar);
        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(true);
        panel.setHeading(Const.CONST_SERVER_NAME + " " + Const.CONST_SERVER_VERSION);
        panel.setFrame(false);

        panel.setCollapsible(true);

        navigationPanel = createNavigationPanel();
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(chartHeight);
        panel.add(navigationPanel, new RowData(1, 1, new Margins(0)));

        add(panel, new FlowData(0));

        chartContainer = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();

        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        chartContainer.setLayout(layout);

        addChart();
        add(chartContainer);


        layout(true);

    }

    private MainMenuBar initToolbar(final LoginInfo loginInfo, Map<String, String> settings) {
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
                        final String logoutUrl = (loginInfo != null) ? loginInfo.getLogoutUrl() : Const.PATH_NIMBITS_HOME;
                        Window.Location.replace(logoutUrl);
                        break;
                    case xmpp:
                        sendXMPPInvite();
                        break;
                    case facebook:
                        Window.Location.replace(Const.PATH_FACEBOOK_APP);
                        break;
                    case twitter:
                        twitterAuthorise();
                        break;
                    case addChart:
                        addChart();
                        break;
                    case save:
                        navigationPanel.saveAll();
                        break;
                }
            }
        });
        return toolBar;
    }

    private void addChart() {

        if (chartContainer.getItemCount() < 2) {


            List<AnnotatedTimeLinePanel> list = new ArrayList<AnnotatedTimeLinePanel>();
            HBoxLayoutData flex = new HBoxLayoutData(new Margins(1, 1, 1, 1));
            flex.setFlex(1);
            final AnnotatedTimeLinePanel line = new AnnotatedTimeLinePanel(true, Const.DEFAULT_CHART_NAME + (chartContainer.getItemCount() + 1));
            line.setHeight(chartHeight);
            line.setSelected(true);
            line.addListener(Events.OnClick, new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent baseEvent) {
                    for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                        AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
                        line.setSelected(p.getName().equals(line.getName()));
                    }
                }
            });

            line.addChartRemovedClickedListeners(new AnnotatedTimeLinePanel.ChartRemovedListener() {
                @Override
                public void onChartRemovedClicked() {
                    for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                        AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
                        if (p.getName().equals(line.getName())) {
                            chartContainer.remove(p);
                            break;
                        }
                    }
                    for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                        AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);

                        p.refreshSize();

                    }
                    chartContainer.layout(true);

                }
            });

            for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                list.add((AnnotatedTimeLinePanel) chartContainer.getItem(i));
            }
            list.add(line);
            chartContainer.removeAll();
            for (AnnotatedTimeLinePanel l : list) {
                l.refreshSize();
                chartContainer.add(l, flex);
            }
            layout(true);
        }
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





    public void addEntity(final GxtModel entity) {

        switch (entity.getEntityType()) {
            case user:
                break;
            case point:
                chartEntity(entity);
                break;
            case category:
                chartEntity(entity);
            case file:
                showFile(entity.getBaseEntity());
                break;
            case subscription:
                displaySubscription(entity.getBaseEntity());
                break;
            case userConnection:
                break;
            case feed:
                notifyEntityClickedListener(entity);
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




    //chart
    private void chartEntity(final GxtModel model) {
//        for (final AnnotatedTimeLinePanel line : lines.values()) {
//            if (line.isSelected()) {
//                addEntityToLine(model, line);
//                break;
//            }
//        }


    }


    private boolean isOneLineSelected() {
//        for (AnnotatedTimeLinePanel l : lines.values()) {
//            if (l.isSelected()) {
//                return true;
//            }
//        }
        return false;
    }

    //    private void addLinesToBottom() {
//
//        if (!isOneLineSelected() && lines.values().iterator().hasNext()) {
//            lines.values().iterator().next().setSelected(true);
//        }
//        for (final AnnotatedTimeLinePanel l : lines.values()) {
//            if (lines.size() == 1) {
//                l.setSelected(true);
//            }
//            double w = 1.0 / (double) lines.size();
//            l.initChart();
//           // bottom.add(l, new RowData(w, 1, new Margins(0)));
//        }
//
//        doLayout(true);
//    }
    private void addEntityToLine(GxtModel model, AnnotatedTimeLinePanel line) {
        if (model.getEntityType().equals(EntityType.point)) {
            line.addEntityModel(model);
        }
        for ( ModelData m : model.getChildren()) {
            addEntityToLine((GxtModel) m, line);
        }

    }

}
