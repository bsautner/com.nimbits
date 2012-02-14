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

import com.extjs.gxt.ui.client.Style.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;

import java.util.*;

public class MainPanel extends NavigationEventProvider {

    private final CenterPanel center = new CenterPanel();
    private User user;
    private ContentPanel west;
    private NavigationPanel navigationPanel;
    private Map<String, String> settings;

    final NavigationPanel createNavigationPanel(ClientType clientType) {
        final NavigationPanel navTree =
                new NavigationPanel(user, clientType, settings);


        navTree.addEntityClickedListeners(new EntityClickedListener() {

            @Override
            public void onEntityClicked(final Entity c)  {
                 center.addEntity(c);
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


//    public void addPointToTree(final Entity entity) {
//        navigationPanel.addUpdateTreeModel(entity);
//    }

    public MainPanel(final LoginInfo l,
                     final boolean doAndroid,
                     final Map<String, String> settings)   {

        this.settings = settings;
        this.user = l.getUser();
        if (doAndroid) {
            loadAndroidLayout();
        } else {
            loadBorderLayout();
        }


    }

    private void loadAndroidLayout()  {
        final FillLayout layout = new FillLayout();
        //this.emailAddress = loginInfo.getEmailAddress();
        setLayout(layout);


        final ContentPanel west = new ContentPanel();
        final NavigationPanel navigationPanel = createNavigationPanel(ClientType.android);
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(1280);
        //  navigationPanel.setAutoHeight(true);
        west.setHeaderVisible(false);
        west.add(navigationPanel);
        west.setHeight("100%");
        west.setHeading("Navigator");

        add(west);


    }

    public void loadTree() {
        west.removeAll();
        navigationPanel = createNavigationPanel(ClientType.other);
        navigationPanel.setLayout(new FillLayout());

        west.add(navigationPanel);
    }

    private void loadBorderLayout()   {
        final BorderLayout layout = new BorderLayout();
      //  this.emailAddress = loginInfo.getEmailAddress();
        setLayout(layout);

        west = new ContentPanel();
        loadTree();
        west.setHeight("100%");
        west.setHeading("Navigator");

        final BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins(5));

        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(5, 5, 5, 0));

        final BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 175);
        eastData.setSplit(true);
        eastData.setCollapsible(true);
        eastData.setMargins(new Margins(5));

        final BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
                125);
        southData.setSplit(true);
        southData.setCollapsible(true);
        southData.setMargins(new Margins(0, 0, 0, 0));
        west.setLayout(new FillLayout(Orientation.VERTICAL));

        add(west, westData);


        center.setLayout(new FillLayout());
        add(center, centerData);
    }

//    public void addDiagram(final Diagram d) {
//        center.addDiagram(d);
//    }
}
