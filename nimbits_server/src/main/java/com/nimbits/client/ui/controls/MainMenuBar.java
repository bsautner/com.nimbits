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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.user.User;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;

import java.util.ArrayList;
import java.util.Collection;


public class MainMenuBar extends ToolBar {


    private Collection<EntityModifiedListener> entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);

    public MainMenuBar(final User user) {
        addNavigateMenu();
        addOptionsMenu();
        addActionMenu();

        addHelpMenu();

        if (user.getIsAdmin()) {
            addSettingsMenu();
        }


        add(actionMenuItem("Browse REST API", null,
                Action.rest));

        add(new SeparatorMenuItem());

        //if (!isDomain) {
        add(actionMenuItem("Logout", null,
                Action.logout));
        // }

    }

    private void addActionMenu() {
        Button button = new Button("Enable External Services");
        Menu menu = new Menu();


        menu.add(actionMenuItem("Instant Message (XMPP)",
                (Icons.INSTANCE.list_items()),
                Action.xmpp));

        button.setMenu(menu);
        add(button);
    }

    private void addNavigateMenu() {
        Button button = new Button("Navigate");
        Menu menu = new Menu();
        menu.add(actionMenuItem("Toggle Expansion",
                (Icons.INSTANCE.expand()),
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
                (Icons.INSTANCE.Help()),
                "http://groups.google.com/group/nimbits"));
        menu.add(urlMenuItem("nimbits.com",
                (Icons.INSTANCE.Home()),
                "http://www.nimbits.com"));

        button.setMenu(menu);
        add(button);
    }

    private void addSettingsMenu() {

        Button button = new Button("Settings");
        Menu menu = new Menu();

        menu.add(actionMenuItem("Server Settings",
                (Icons.INSTANCE.Grid()),
                Action.admin));


        button.setMenu(menu);
        add(button);
    }


    private MenuItem actionMenuItem(final String text,
                                    final AbstractImagePrototype icon,
                                    final Action action) {
        MenuItem item = new MenuItem(text);

        if (icon != null) {
            item.setIcon(icon);
        }

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


    }

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }


    private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>(1);

    public interface ActionListener {
        void onAction(Action action) throws ValueException;

    }

    public void addActionListeners(final ActionListener listener) {
        this.actionListeners.add(listener);
    }

    void notifyActionListener(Action action) throws ValueException {
        for (ActionListener listener : actionListeners) {
            listener.onAction(action);
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


}





