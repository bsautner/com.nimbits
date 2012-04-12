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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.menu.*;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.user.*;
import com.nimbits.client.ui.helper.*;
import com.nimbits.client.ui.icons.*;
import com.nimbits.client.ui.panels.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/23/12
 * Time: 3:55 PM
 */
public class MainMenuBar extends ToolBar {
    private static final int MAX_HEIGHT = 200;
    private UserServiceAsync service;
    private int connectionCount = 0;
    private  LoginInfo loginInfo;
    private Map<SettingType, String> settings;

    private final Listener<MessageBoxEvent> createNewFolderListener = new NewFolderMessageBoxEventListener();
    private final Listener<MessageBoxEvent> createNewPointListener = new NewPointMessageBoxEventListener();
    private final Listener<MessageBoxEvent> newKeyListener= new NewKeyMessageBoxEventListener();
    private final Listener<BaseEvent> uploadFileListener = new UploadFileBaseEventListener();
    private Collection<EntityModifiedListener> entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);

    public MainMenuBar(LoginInfo loginInfo, Map<SettingType, String> settings) throws NimbitsException {
        this.loginInfo = loginInfo;
        this.settings = settings;

        service = GWT.create(UserService.class);

        addFileMenu();
        addNavigateMenu();
        addActionMenu();
        addOptionsMenu();

        if (loginInfo.isUserAdmin()) {
            addAdminMenu();
        }
        addHelpMenu();
        add(new SeparatorMenuItem());

        //  add(saveButton());
        add(addChartButton());

        add(connectionButton());
        add(pendingConnectionsButton());

        add(urlMenuItem("Report Issue",
                AbstractImagePrototype.create(Icons.INSTANCE.bug()),
                "https://github.com/bsautner/com.nimbits/issues"));

        add(actionMenuItem("Logout",
                AbstractImagePrototype.create(Icons.INSTANCE.deleteFriend()),
                Action.logout));

    }

    private void addFileMenu() {
        Button fileButton = new Button("File");
        Menu fileMenu = new Menu();
        fileMenu.add(newDataPoint());
        fileMenu.add(newFolder());
        fileMenu.add(uploadFile());


        fileButton.setMenu(fileMenu);
        add(fileButton);
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
    private void addAdminMenu() {
        Button button = new Button("Admin");
        Menu menu = new Menu();
        menu.add(urlMenuItem("Run System Maintenance Service",
                AbstractImagePrototype.create(Icons.INSTANCE.expand()),
                "http://" + com.google.gwt.user.client.Window.Location.getHostName()+ "/cron/SystemMaint"));


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
    private void addActionMenu() {
        Button button = new Button("Action");
        Menu menu = new Menu();

        menu.add(newKeyButton());

        if (settings.containsKey(SettingType.twitterClientId) && !Utils.isEmptyString(settings.get(SettingType.twitterClientId)) && loginInfo != null)
        {
            menu.add(actionMenuItem("Enable Facebook",
                    AbstractImagePrototype.create(Icons.INSTANCE.connection()),
                    Action.facebook));
        }
        if (settings.containsKey(SettingType.facebookAPIKey) && !Utils.isEmptyString(settings.get(SettingType.facebookAPIKey)))
        {
            menu.add(actionMenuItem("Enable Twitter",
                    AbstractImagePrototype.create(Icons.INSTANCE.connection()),
                    Action.twitter));
        }
        menu.add(actionMenuItem("Enable Instant Message (XMPP)",
                AbstractImagePrototype.create(Icons.INSTANCE.list_items()),
                Action.xmpp));

        button.setMenu(menu);
        add(button);
    }

    private MenuItem newDataPoint() {
        MenuItem item = new MenuItem("Data Point");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addNew()));
        item.setToolTip(UserMessages.MESSAGE_NEW_POINT);
        item.addListener(Events.OnClick, new NewPointBaseEventListener());

        return item;


    }

    private MenuItem uploadFile() {
        MenuItem item = new MenuItem("Upload File");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));

        item.addListener(Events.OnClick, uploadFileListener);

        return item;


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



    private MenuItem newFolder() {
        MenuItem item = new MenuItem("New Folder");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.category()));

        item.addListener(Events.OnClick, new AddFolderBaseEventListener());

        return item;


    }
    private MenuItem newKeyButton() {
        MenuItem item = new MenuItem("Get or Reset Secret Key");
        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Key()));
        item.addListener(Events.OnClick, new ResetSecretBaseEventListener());
        return item;
    }


    private Button pendingConnectionsButton() throws NimbitsException {
        final Button connectionRequest = new Button("Connection Requests(" + connectionCount + ')');

        connectionRequest.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.add16()));
        service.getPendingConnectionRequests(loginInfo.getEmailAddress(), new GetPendingRequestListAsyncCallback(connectionRequest));
        return connectionRequest;
    }
    public interface EntityModifiedListener {
        void onEntityModified(final TreeModel model, final Action action) throws NimbitsException;

    }

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    void notifyEntityModifiedListener(final TreeModel model, final Action action) throws NimbitsException {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }


    private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>(1);

    public interface ActionListener {
        void onAction(Action action) throws NimbitsException;

    }

    public void addActionListeners(final ActionListener listener) {
        this.actionListeners.add(listener);
    }

    void notifyActionListener(Action action) throws NimbitsException {
        for (ActionListener listener : actionListeners) {
            listener.onAction(action);
        }
    }

    private static Button connectionButton() {
        final Button b = new Button("&nbsp;Connect");
        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.email2()));

        b.addListener(Events.OnClick, new ConnectBaseEventListener());
        return b;
    }

    private static Listener<MessageBoxEvent> sendInviteListener() {
        return new MessageBoxEventListener();
    }

    private Button addChartButton() {
        Button addChartButton = new Button("&nbsp;Add Chart");
        addChartButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.lineChart()));
        addChartButton.setToolTip("Add another chart");

        addChartButton.addListener(Events.OnClick, new AddChartBaseEventListener());
        return addChartButton;

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

    private static class MessageBoxEventListener implements Listener<MessageBoxEvent> {
        MessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            final String email = be.getValue();
            if (email != null) {
                if (!email.isEmpty()) {
                    UserServiceAsync userService = GWT.create(UserService.class);
                    try {
                        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);

                        userService.sendConnectionRequest(emailAddress, new ConnectionRequestAsyncCallback());
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }

                }
            }
        }

    }

    private static class ConnectBaseEventListener implements Listener<BaseEvent> {

        ConnectBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent be) {


            final MessageBox box = MessageBox.prompt(UserMessages.MESSAGE_CONNECTION_REQUEST_TITLE, UserMessages.MESSAGE_CONNECTION_REQUEST);
            box.addCallback(sendInviteListener());

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

    private class AddChartBaseEventListener implements Listener<BaseEvent> {
        AddChartBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent baseEvent) {
            try {
                notifyActionListener(Action.addChart);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
    }

    private class NewPointBaseEventListener implements Listener<BaseEvent> {
        NewPointBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                    UserMessages.MESSAGE_NEW_POINT,
                    UserMessages.MESSAGE_NEW_POINT_PROMPT);

            box.addCallback(createNewPointListener);
        }
    }

    private class FileUploadListener implements FileUploadPanel.FileAddedListener {

        private final Window w;

        FileUploadListener(Window w) {
            this.w = w;
        }

        @Override
        public void onFileAdded() throws NimbitsException {
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
            } catch (NimbitsException e) {
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
            FileUploadPanel p = new FileUploadPanel(UploadType.newFile);
            p.addFileAddedListeners(new FileUploadListener(w));

            w.add(p);
            w.show();
        }
    }

    private class AddFolderBaseEventListener implements Listener<BaseEvent> {
        AddFolderBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                    UserMessages.MESSAGE_ADD_CATEGORY,
                    "Add a new folder to organize your data. Folders can be shared, and " +
                            "subscribed to by other users if you set their security level " +
                            "to public");

            box.addCallback(createNewFolderListener);
        }
    }

    private class ResetSecretBaseEventListener implements Listener<BaseEvent> {

        ResetSecretBaseEventListener() {
        }

        @Override
        public void handleEvent(BaseEvent be) {
            service.getSecret(new ResetSecretAsyncCallback());

        }

        private class ResetSecretAsyncCallback implements AsyncCallback<String> {
            ResetSecretAsyncCallback() {
            }

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(String s) {
                MessageBox.confirm("Reset Your Key",
                        "Your secret Key is currently set to: " + s +
                                "<br> Press YES to generate a new secret key and to have it emailed to the account you are currently logged in with. " +
                                "Your old key will no longer be valid. You can use your key to use Nimbits web services.",
                        newKeyListener);

            }
        }
    }

    private static class ResetSecretAsyncCallback implements AsyncCallback<String> {

        ResetSecretAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {

            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(String key) {
            com.google.gwt.user.client.Window.alert("Your new secret has been reset to: " + key + " and a copy has been emailed to you. Your old secret key is no longer valid.");

        }

    }

    private class ApproveConnectionMessageBoxEventListener implements Listener<MessageBoxEvent> {
        private final ConnectionRequest r;
        private final Menu scrollMenu;
        private final MenuItem m;
        private final Button connectionRequest;

        ApproveConnectionMessageBoxEventListener(ConnectionRequest r, Menu scrollMenu, MenuItem m, Button connectionRequest) {
            this.r = r;
            this.scrollMenu = scrollMenu;
            this.m = m;
            this.connectionRequest = connectionRequest;
        }

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
            }
            catch (NimbitsException ex) {
                FeedbackHelper.showError(ex);
            }
        }

        private void acceptConnection(
                final ConnectionRequest r,
                boolean accepted) throws NimbitsException {
            UserServiceAsync userService = GWT.create(UserService.class);
            userService.connectionRequestReply(r.getTargetEmail(), r.getRequestorEmail(), r.getKey(), accepted, new AcceptConnectionAsyncCallback());
        }


        private class AcceptConnectionAsyncCallback implements AsyncCallback<Void> {

            AcceptConnectionAsyncCallback() {
            }

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(Void result) {

                connectionCount += -1;

                connectionRequest.setText("Requests(" + connectionCount + ')');
                try {
                    notifyEntityModifiedListener(null, Action.refresh);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }

        }
    }

    private class AcceptConnectionBaseEventListener implements Listener<BaseEvent> {

        private final ConnectionRequest r;
        private final Menu scrollMenu;
        private final MenuItem m;
        private final Button connectionRequest;

        AcceptConnectionBaseEventListener(ConnectionRequest r, Menu scrollMenu, MenuItem m, Button connectionRequest) {
            this.r = r;
            this.scrollMenu = scrollMenu;
            this.m = m;
            this.connectionRequest = connectionRequest;
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            //	final Dialog simple = new Dialog();
            //simple.setHeading(");
            try {
                final MessageBox box = new MessageBox();
                box.setButtons(MessageBox.YESNOCANCEL);
                box.setIcon(MessageBox.QUESTION);
                box.setTitle("Connection request approval");
                box.addCallback(new ApproveConnectionMessageBoxEventListener(r, scrollMenu, m, connectionRequest));


                box.setMessage("The owner of the email address: '" + r.getRequestorEmail().getValue() + "' would like to connect with you. You will have read only access to each others data points. Is that OK?");

                box.show();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }


        }

    }

    private class AddUpdateEntityAsyncCallback implements AsyncCallback<Entity> {
        AddUpdateEntityAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final Entity result) {
            try {
                notifyEntityModifiedListener(new GxtModel(result), Action.create);
            } catch (NimbitsException e) {
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
                EntityServiceAsync service = GWT.create(EntityService.class);

                try {
                    EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName, EntityType.point);
                    Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                    Point p = PointModelFactory.createPointModel(entity);
                    //     Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                    service.addUpdateEntity(p, new NewPointEntityAsyncCallback(box));
                } catch (NimbitsException caught) {
                    FeedbackHelper.showError(caught);

                }

            }
        }

        private class NewPointEntityAsyncCallback implements AsyncCallback<Entity> {
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
            public void onSuccess(Entity result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result), Action.create);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

                box.close();
            }
        }
    }

    private class GetPendingRequestListAsyncCallback implements AsyncCallback<List<ConnectionRequest>> {

        private final Button connectionRequest;

        GetPendingRequestListAsyncCallback(Button connectionRequest) {
            this.connectionRequest = connectionRequest;
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);

        }

        @Override
        public void onSuccess(final List<ConnectionRequest> result) {

            try {
                if (result.isEmpty()) {
                    connectionRequest.setVisible(false);
                } else {
                    final Menu scrollMenu = new Menu();
                    scrollMenu.setMaxHeight(MAX_HEIGHT);
                    for (final ConnectionRequest r : result) {
                        MenuItem m = acceptConnectionMenuItem(scrollMenu, r);
                        scrollMenu.add(m);
                    }


                    connectionRequest.setMenu(scrollMenu);
                    connectionCount = result.size();

                    connectionRequest.setText("Requests(" + connectionCount + ')');
                }
            }
            catch (NimbitsException ex) {
                FeedbackHelper.showError(ex);
            }
        }

        private MenuItem acceptConnectionMenuItem(final Menu scrollMenu, final ConnectionRequest r) throws NimbitsException {
            final MenuItem m = new MenuItem(r.getRequestorEmail().getValue());
            m.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connection()));
            m.addListener(Events.Select, new AcceptConnectionBaseEventListener(r, scrollMenu, m, connectionRequest));
            return m;
        }


    }

    private class NewFolderMessageBoxEventListener implements Listener<MessageBoxEvent> {

        NewFolderMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(final MessageBoxEvent be) {
            final String newEntityName = be.getValue();
            if (! Utils.isEmptyString(newEntityName))  {
                final EntityName categoryName;
                try {
                    categoryName = CommonFactoryLocator.getInstance().createName(newEntityName, EntityType.category);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                    return;
                }

                final EntityServiceAsync service = GWT.create(EntityService.class);
                Entity entity = EntityModelFactory.createEntity(categoryName, EntityType.category);

                service.addUpdateEntity(entity,
                        new AddUpdateEntityAsyncCallback());


            }
        }
    }

    private static class NewKeyMessageBoxEventListener implements Listener<MessageBoxEvent> {
        NewKeyMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final UserServiceAsync us = GWT.create(UserService.class);

            if (btn.getText().toLowerCase().equals("yes")) {
                us.updateSecret(new ResetSecretAsyncCallback());

            }

        }
    }
}
