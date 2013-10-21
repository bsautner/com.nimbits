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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;
import com.nimbits.client.ui.panels.FileUploadPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/23/12
 * Time: 3:55 PM
 */
public class MainMenuBar extends ToolBar {



    private Collection<EntityModifiedListener> entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);

    public MainMenuBar(final User user) {
        addNavigateMenu();
        addOptionsMenu();
        addActionMenu();

        addHelpMenu();
        add(new SeparatorMenuItem());


        //if (!isDomain) {
            add(actionMenuItem("Logout",
                    AbstractImagePrototype.create(Icons.INSTANCE.deleteFriend()),
                    Action.logout));
       // }

    }
    private void addActionMenu() {
        Button button = new Button("Enable External Services");
        Menu menu = new Menu();


        menu.add(actionMenuItem("Instant Message (XMPP)",
                AbstractImagePrototype.create(Icons.INSTANCE.list_items()),
                Action.xmpp));

        button.setMenu(menu);
        add(button);
    }

    private void addNavigateMenu() {
        Button button = new Button("Navigate");
        Menu menu = new Menu();
        menu.add(actionMenuItem("Toggle Expansion",
                AbstractImagePrototype.create(Icons.INSTANCE.expand()),
                Action.expand));


        button.setMenu(menu);
        add(button);
    }

    private void addOptionsMenu() {
        Button button = new Button("Options");
        Menu menu = new Menu();


        CheckBox saveToNowCheckBox = new CheckBox();

        menu.add(saveToNowCheckBox);
        saveToNowCheckBox.setBoxLabel("Save with Current Time");
        saveToNowCheckBox.setValue(true);
//        autoSaveCheckBox.setBoxLabel("Auto-Save when a number is entered");
//        autoSaveCheckBox.setValue(true);
        // menu.add(autoSaveCheckBox);
        button.setMenu(menu);
        add(button);
    }



    private void addHelpMenu() {
        Button button = new Button("Help");
        Menu menu = new Menu();

        menu.add(urlMenuItem("Forum",
                AbstractImagePrototype.create(Icons.INSTANCE.Help()),
                "http://groups.google.com/group/nimbits"));
        menu.add(urlMenuItem("nimbits.com",
                AbstractImagePrototype.create(Icons.INSTANCE.Home()),
                "http://www.nimbits.com"));

        button.setMenu(menu);
        add(button);
    }




    private MenuItem actionMenuItem(final String text,
                                    final AbstractImagePrototype icon,
                                    final Action action) {
        MenuItem item = new MenuItem(text);

        item.setIcon(icon);

        item.addListener(Events.OnClick, new ActionEventListener(action));

        return item;


    }

    private static MenuItem urlMenuItem(final String text,
                                        final AbstractImagePrototype icon,
                                        final String url) {
        MenuItem item = new MenuItem(text);

        item.setIcon(icon);

        item.addListener(Events.OnClick, new OpenUrlBaseEventListener(url));

        return item;


    }




    public interface EntityModifiedListener {
        void onEntityModified(final TreeModel model, final Action action);

    }

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    void notifyEntityModifiedListener(final TreeModel model, final Action action) {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }


    private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>(1);

    public interface ActionListener {
        void onAction(Action action);

    }

    public void addActionListeners(final ActionListener listener) {
        this.actionListeners.add(listener);
    }

    void notifyActionListener(Action action) {
        for (ActionListener listener : actionListeners) {
            listener.onAction(action);
        }
    }






    private static class ConnectionRequestAsyncCallback implements AsyncCallback<Void> {

        ConnectionRequestAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);

        }

        @Override
        public void onSuccess(Void result) {

            Info.display(UserMessages.MESSAGE_CONNECTION_REQUEST_TITLE, UserMessages.MESSAGE_CONNECTION_REQUEST_SUCCESS);

        }

    }





    private static class OpenUrlBaseEventListener implements Listener<BaseEvent> {
        private final String url;

        OpenUrlBaseEventListener(String url) {
            this.url = url;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            com.google.gwt.user.client.Window.open(url, "", "");
        }
    }



    private class FileUploadListener implements FileUploadPanel.FileAddedListener {

        private final Window w;

        FileUploadListener(Window w) {
            this.w = w;
        }

        @Override
        public void onFileAdded() {
            w.hide();
            notifyEntityModifiedListener(null, Action.refresh);

        }
    }

    private class ActionEventListener implements Listener<BaseEvent> {
        private final Action action;

        ActionEventListener(Action action) {
            this.action = action;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            try {
                notifyActionListener(action);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class UploadFileBaseEventListener implements Listener<BaseEvent> {


        UploadFileBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent be) {
            final Window w = new Window();
            w.setAutoWidth(true);
            w.setHeading(UserMessages.MESSAGE_UPLOAD_SVG);

            FileUploadPanel p = new FileUploadPanel();
            p.addFileAddedListeners(new FileUploadListener(w));

            w.add(p);
            w.show();
        }
    }







    private class AddUpdateEntityAsyncCallback implements AsyncCallback<List<Entity>> {
        AddUpdateEntityAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final List<Entity> result) {
            try {
                notifyEntityModifiedListener(new GxtModel(result.get(0)), Action.create);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

        }
    }

    private class NewPointMessageBoxEventListener implements Listener<MessageBoxEvent> {

        private String newEntityName;

        NewPointMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

                try {

                    EntityName name = CommonFactory.createName(newEntityName, EntityType.point);
                    Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                    Point p = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.basic, 0, false, 0.0);

                    service.addUpdateEntityRpc(Arrays.<Entity>asList(p), new NewPointEntityAsyncCallback(box));
                } catch (Exception caught) {
                    FeedbackHelper.showError(caught);

                }

            }
        }


        private class NewPointEntityAsyncCallback implements AsyncCallback<List<Entity>> {
            private final MessageBox box;

            NewPointEntityAsyncCallback(MessageBox box) {
                this.box = box;
            }

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
                box.close();
            }

            @Override
            public void onSuccess(List<Entity> result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result.get(0)), Action.create);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

                box.close();
            }
        }
    }




    private class NewFolderMessageBoxEventListener implements Listener<MessageBoxEvent> {

        NewFolderMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(final MessageBoxEvent be) {
            final String newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final EntityName categoryName;
                try {
                    categoryName = CommonFactory.createName(newEntityName, EntityType.category);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                    return;
                }

                final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
                Entity entity = EntityModelFactory.createEntity(categoryName, EntityType.category);

                service.addUpdateEntityRpc(Arrays.<Entity>asList(entity),
                        new AddUpdateEntityAsyncCallback());


            }
        }
    }


}