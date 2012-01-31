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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.user.*;

import java.util.*;

class UserListPanel extends NavigationEventProvider {
    private final UserServiceAsync userService = GWT.create(UserService.class);
    // private NavigationPanel navTree;// = new NavTree();
    private final EmailAddress email;


    public UserListPanel(EmailAddress email) {
        setLayout(new FlowLayout(10));
        this.email = email;

    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        final ListStore<GxtUserModel> store = new ListStore<GxtUserModel>();


        userService.getConnections(email, new AsyncCallback<List<User>>() {

            @Override
            public void onFailure(Throwable caught) {


            }

            @Override
            public void onSuccess(List<User> result) {

                for (User n : result) {

                    store.add(new GxtUserModel(n));

                }
            }

        });


        final ContentPanel panel = new ContentPanel();
        panel.setCollapsible(false);
        panel.setAnimCollapse(false);
        panel.setFrame(false);
        panel.setHeaderVisible(false);


        panel.setBorders(false);
        panel.setId("images-view");

        // panel.setWidth(535);
        panel.setAutoHeight(true);
        panel.setBodyBorder(false);

        ListView<GxtUserModel> view = new ListView<GxtUserModel>() {
            @Override
            protected GxtUserModel prepareData(GxtUserModel model) {
                String s = model.get(Const.PARAM_NAME);
                model.set("shortName", Format.ellipse(s, 15));
                model.set(Const.PARAM_PATH, model.get(Const.PARAM_PATH));

                return model;
            }

        };

        view.setTemplate(getTemplate());
        view.setBorders(false);

        view.setStore(store);
        view.setItemSelector("div.thumb-wrap");
        view.setWidth(100);
        view.getSelectionModel().addListener(Events.SelectionChange,
                new Listener<SelectionChangedEvent<GxtUserModel>>() {

                    public void handleEvent(SelectionChangedEvent<GxtUserModel> be) {

                        final NavigationPanel navigationPanel = createNavigationPanel(be.getSelectedItem().getEmail());
                        navigationPanel.setLayout(new FillLayout());


//                        initTree(true);
                        //navigationPanel.setHeight(400);
                        panel.removeAll();
                        panel.add(navigationPanel);
                        panel.setHeight("100%");
                        //  panel.setHeight(800);
                        //	 panel.setHeaderVisible(true);
                        // panel.setHeading(be.getSelectedItem().getValue());
                        doLayout();


                    }

                });
        panel.add(view);
        add(panel);
    }

    final NavigationPanel createNavigationPanel(final EmailAddress selectedEmail) {

        final NavigationPanel navTree = new NavigationPanel(selectedEmail, true, ClientType.other);

        //  navTree.loadAuthTree();

        navTree.addCategoryClickedListeners(new CategoryClickedListener() {

            @Override
            public void onCategoryClicked(final Category c, boolean readOnly)  {

                notifyCategoryClickedListener(c, readOnly);

            }

        });

        navTree.addPointClickedListeners(new PointClickedListener() {

            @Override
            public void onPointClicked(final Point p){

                notifyPointClickedListener(p);
            }

        });

        navTree.addDiagramClickedListeners(new DiagramClickedListener() {

            @Override
            public void onDiagramClicked(final Diagram p) {

                notifyDiagramClickedListener(p);
            }

        });


        navTree.addDiagramDeletedListeners(new DiagramDeletedListener() {

            @Override
            public void onDiagramDeleted(final Diagram c, final boolean readOnly) {
                notifyDiagramDeletedListener(c, readOnly);
            }

        });
        return navTree;


    }

//    private NavigationPanel createTree() {
//        final NavigationPanel navTree = new NavigationPanel(anEmailAddress, true);
//        navTree.addCategoryClickedListeners(new CategoryClickedListener() {
//            @Override
//            public void onCategoryClicked(final Category c, final boolean readOnly) throws NimbitsException {
//                notifyCategoryClickedListener(c, readOnly);
//            }
//
//        });
//
//        navTree.addPointClickedListeners(new PointClickedListener() {
//
//            @Override
//            public void onPointClicked(final Point p) {
//
//                notifyPointClickedListener(p);
//            }
//
//        });
//        navTree.addDiagramClickedListeners(new NavigationEventProvider.DiagramClickedListener() {
//
//                    @Override
//                    public void onDiagramClicked(Diagram p, String target) {
//
//                        notifyDiagramClickedListener(p, Const.WORD_BLANK);
//                    }
//
//                });
//        return navTree;
//
//    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<div class="thumb-wrap" id="{name}" style="border: 0px solid white">',

            '<div class="thumb"><img border="0px" src="{path}" title="{name}"></div>',
            '<span class="x-editable">{shortName}</span></div>',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");

    }-*/;

}
